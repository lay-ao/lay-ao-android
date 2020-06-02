package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.designbyark.layao.adapters.BannerSliderAdapter.BannerSliderAdapterVH
import com.designbyark.layao.data.Banner
import com.designbyark.layao.databinding.BodyBannerBinding
import com.smarteist.autoimageslider.SliderViewAdapter

class BannerSliderAdapter(
    private val mSliderItems: List<Banner>,
    private val mBannerItemClickListener: BannerItemClickListener
) : SliderViewAdapter<BannerSliderAdapterVH>() {

    override fun getCount(): Int {
        return mSliderItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup): BannerSliderAdapterVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BodyBannerBinding.inflate(layoutInflater, parent, false)
        return BannerSliderAdapterVH(binding)
    }

    override fun onBindViewHolder(holder: BannerSliderAdapterVH, position: Int) {
        holder.bind(mSliderItems[position])
        holder.rootView.setOnClickListener {
            mBannerItemClickListener.mBannerItemClickListener(mSliderItems[position])
        }
    }

    class BannerSliderAdapterVH(private val binding: BodyBannerBinding) :
        ViewHolder(binding.root) {

        val rootView = binding.root
        fun bind(banner: Banner) {
            binding.banner = banner
            binding.executePendingBindings()
        }

    }

    interface BannerItemClickListener {
        fun mBannerItemClickListener(banner: Banner)
    }

}