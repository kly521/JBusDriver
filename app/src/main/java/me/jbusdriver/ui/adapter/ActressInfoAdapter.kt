package me.jbusdriver.ui.adapter

import android.graphics.Bitmap
import android.support.v7.graphics.Palette
import android.text.TextUtils
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import jbusdriver.me.jbusdriver.R
import me.jbusdriver.common.*
import me.jbusdriver.mvp.bean.ActressInfo
import java.util.*

/**
 * Created by Administrator on 2017/7/17.
 */
class ActressInfoAdapter(val rxManager: CompositeDisposable) : BaseQuickAdapter<ActressInfo, BaseViewHolder>(R.layout.layout_actress_item) {
    private val random = Random()
    private fun randomNum(number: Int) = Math.abs(random.nextInt() % number)

    override fun convert(holder: BaseViewHolder, item: ActressInfo) {
        KLog.d("ActressInfo :$item")
        GlideApp.with(holder.itemView.context).asBitmap().load(item.avatar.toGlideUrl)
                .error(R.drawable.ic_nowprinting).into(object : BitmapImageViewTarget(holder.getView(R.id.iv_actress_avatar)) {
            override fun onResourceReady(resource: Bitmap?, transition: Transition<in Bitmap>?) {
                resource?.let {
                    Flowable.just(it).map {
                        Palette.from(it).generate()
                    }.compose(SchedulersCompat.io())
                            .subscribeWith(object : SimpleSubscriber<Palette>() {
                                override fun onNext(it: Palette) {
                                    super.onNext(it)
                                    val swatch = listOfNotNull(it.lightMutedSwatch, it.lightVibrantSwatch, it.vibrantSwatch, it.mutedSwatch)
                                    if (!swatch.isEmpty()) {
                                        swatch[randomNum(swatch.size)].let {
                                            holder.setBackgroundColor(R.id.tv_actress_name, it.rgb)
                                            holder.setTextColor(R.id.tv_actress_name, it.bodyTextColor)
                                        }
                                    }
                                }
                            })
                            .addTo(rxManager)
                }

                super.onResourceReady(resource, transition)
            }
        })
        //加载名字
        holder.setText(R.id.tv_actress_name, item.name)

        holder.setText(R.id.tv_actress_tag, item.tag)
        holder.setVisible(R.id.tv_actress_tag, !TextUtils.isEmpty(item.tag))
    }


}