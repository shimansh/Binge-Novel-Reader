package com.mgn.bingenovelreader.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.mgn.bingenovelreader.R
import com.mgn.bingenovelreader.adapter.GenericAdapter
import com.mgn.bingenovelreader.database.*
import com.mgn.bingenovelreader.dbHelper
import com.mgn.bingenovelreader.event.EventType
import com.mgn.bingenovelreader.event.NovelEvent
import com.mgn.bingenovelreader.model.DownloadQueue
import com.mgn.bingenovelreader.model.Novel
import com.mgn.bingenovelreader.service.DownloadService
import com.mgn.bingenovelreader.util.Constants
import com.mgn.bingenovelreader.util.Util
import com.mgn.bingenovelreader.util.setDefaults
import kotlinx.android.synthetic.main.activity_download_queue.*
import kotlinx.android.synthetic.main.content_download_queue.*
import kotlinx.android.synthetic.main.listitem_download_queue.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.Bold
import org.jetbrains.anko.append
import org.jetbrains.anko.buildSpanned
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import java.io.File


class DownloadFragment : BaseFragment(), GenericAdapter.Listener<DownloadQueue> {

    lateinit var adapter: GenericAdapter<DownloadQueue>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.activity_download_queue, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar.title = getString(R.string.title_downloads)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        setRecyclerView()

        if (DownloadService.IS_DOWNLOADING) {
            fab.setImageResource(R.drawable.ic_pause_black_vector)
            fab.tag = "playing"
        } else {
            fab.setImageResource(R.drawable.ic_play_arrow_black_vector)
            fab.tag = "paused"
        }

        fab.setOnClickListener {
            if (!adapter.items.isEmpty()) {
                if (fab.tag == "playing") {
                    //pause all
                    dbHelper.updateAllDownloadQueueStatuses(Constants.STATUS_STOPPED)
                    fab.setImageResource(R.drawable.ic_play_arrow_black_vector)
                    fab.tag = "paused"
                } else if (fab.tag == "paused") {
                    //play all
                    dbHelper.updateAllDownloadQueueStatuses(Constants.STATUS_DOWNLOAD)
                    fab.setImageResource(R.drawable.ic_pause_black_vector)
                    fab.tag = "playing"
                    startDownloadService(adapter.items[0].novelId)
                }
                adapter.updateData(ArrayList(dbHelper.getAllDownloadQueue()))
            }
        }

    }


    private fun setRecyclerView() {
        val items = dbHelper.getAllDownloadQueue()
        adapter = GenericAdapter(ArrayList(items), R.layout.listitem_download_queue, this)
        recyclerView.setDefaults(adapter)
        recyclerView.addItemDecoration(object : DividerItemDecoration(context, DividerItemDecoration.VERTICAL) {

            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                val position = parent?.getChildAdapterPosition(view)
                if (position == parent?.adapter?.itemCount?.minus(1)) {
                    outRect?.setEmpty()
                } else {
                    super.getItemOffsets(outRect, view, parent, state)
                }
            }
        })
    }

    //region Adapter Listener Methods - onItemClick(), viewBinder()
    @SuppressLint("SetTextI18n")
    override fun bind(item: DownloadQueue, itemView: View) {
        val novel = dbHelper.getNovel(item.novelId)
        itemView.novelName.text = novel?.name
        if (novel?.imageFilePath != null)
            Glide.with(this).load(File(novel.imageFilePath)).into(itemView.novelIcon)



        if (item.status.toInt() == Constants.STATUS_DOWNLOAD) {
            itemView.downloadPauseButton.setImageResource(R.drawable.ic_pause_black_vector)
        } else {
            itemView.downloadPauseButton.setImageResource(R.drawable.ic_play_arrow_black_vector)
        }
        val totalChapterCount = dbHelper.getAllWebPages(novelId = novel?.id!!).count()
        if (totalChapterCount != 0) {
            val displayText = "${dbHelper.getAllReadableWebPages(novel.id).count()}/$totalChapterCount"
            itemView.downloadProgress.text = displayText
        } else {
            itemView.downloadProgress.text = "Waiting to download…"
        }

        val preText = if (DownloadService.NOVEL_ID != novel.id) "In Queue - " else "Downloading: "
        val displayText = preText + itemView.downloadProgress.text.toString()
        itemView.downloadProgress.text = displayText

        itemView.downloadPauseButton.setOnClickListener {
            if (item.status.toInt() == Constants.STATUS_DOWNLOAD) {
                dbHelper.updateDownloadQueueStatus(Constants.STATUS_STOPPED.toLong(), item.novelId)
                itemView.downloadPauseButton.setImageResource(R.drawable.ic_play_arrow_black_vector)
                if (dbHelper.getFirstDownloadableQueueItem() == null) {
                    fab.setImageResource(R.drawable.ic_play_arrow_black_vector)
                    fab.tag = "paused"
                }

            } else if (item.status.toInt() == Constants.STATUS_STOPPED) {
                dbHelper.updateDownloadQueueStatus(Constants.STATUS_DOWNLOAD.toLong(), item.novelId)
                itemView.downloadPauseButton.setImageResource(R.drawable.ic_pause_black_vector)
                startDownloadService(item.novelId)
                fab.setImageResource(R.drawable.ic_pause_black_vector)
                fab.tag = "playing"
            }
        }

        itemView.downloadDeleteButton.setOnClickListener {
            confirmDeleteAlert(novel)
        }
    }

    override fun onItemClick(item: DownloadQueue) {
        // Do Nothing
        //        val serviceIntent = Intent(this, DownloadService::class.java)
        //        startService(serviceIntent)
    }
    //endregion

    private fun startDownloadService(novelId: Long) {
        val serviceIntent = Intent(activity, DownloadService::class.java)
        serviceIntent.putExtra(Constants.NOVEL_ID, novelId)
        activity.startService(serviceIntent)
    }


    private fun confirmDeleteAlert(novel: Novel) {
        //TODO: Need to make the text spannable
        alert(buildSpanned {
            append("Delete ")
            append("${novel.name}", Bold)
            append("?")
        }.toString(), "Confirm Delete") {
            positiveButton("Yesh~") { deleteNovel(novel) }
            negativeButton("Never Mind!") { }
        }.show()
    }

    private fun deleteNovel(novel: Novel) {
        Util.deleteNovel(context, novel)
        val index = adapter.items.indexOfFirst { it.novelId == novel.id }
        if (index != -1) adapter.removeItemAt(index)
        if (adapter.items.size == 0) {
            fab.setImageResource(R.drawable.ic_play_arrow_black_vector)
            fab.tag = "paused"
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNovelEvent(event: NovelEvent) {
        when (event.type) {
            EventType.UPDATE -> {
                if (event.novelId == -1L) {
                    if (!Util.checkNetwork(context)) {
                        toast("No Active Internet! (⋋▂⋌)")
                        fab.setImageResource(R.drawable.ic_play_arrow_black_vector)
                        fab.tag = "paused"
                        adapter.updateData(ArrayList(dbHelper.getAllDownloadQueue()))
                    }
                } else {
                    val dq = dbHelper.getDownloadQueue(event.novelId)
                    if (dq != null)
                        adapter.updateItem(dq)
                }
            }
            EventType.COMPLETE -> {
                val dq = DownloadQueue()
                dq.novelId = event.novelId
                adapter.removeItem(dq)
                if (adapter.items.size == 0) {
                    fab.setImageResource(R.drawable.ic_play_arrow_black_vector)
                    fab.tag = "paused"
                }
            }
            else -> {
            }
        }
    }
}
