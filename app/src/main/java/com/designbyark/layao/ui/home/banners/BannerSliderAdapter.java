package com.designbyark.layao.ui.home.banners;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.designbyark.layao.R;
import com.designbyark.layao.data.Banner;
import com.smarteist.autoimageslider.SliderViewAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.designbyark.layao.common.GlobalsKt.circularProgressBar;

public class BannerSliderAdapter extends SliderViewAdapter<BannerSliderAdapter.BannerSliderAdapterVH> {

    private Context context;
    private List<Banner> mSliderItems;
    private BannerItemClickListener mBannerItemClickListener;

    public BannerSliderAdapter(Context context, List<Banner> mSliderItems, BannerItemClickListener mBannerItemClickListener) {
        this.context = context;
        this.mSliderItems = mSliderItems;
        this.mBannerItemClickListener = mBannerItemClickListener;
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    @Override
    public BannerSliderAdapterVH onCreateViewHolder(@NotNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.body_banner, parent, false);
        return new BannerSliderAdapterVH(view);
    }

    @Override
    public void onBindViewHolder(@NotNull BannerSliderAdapterVH viewHolder, int position) {

        final Banner model = mSliderItems.get(position);
        Glide.with(context)
                .load(model.getImage())
                .placeholder(circularProgressBar(context))
                .into(viewHolder.imageView);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBannerItemClickListener.mBannerItemClickListener(model.getId());
            }
        });

    }

     static class BannerSliderAdapterVH extends SliderViewAdapter.ViewHolder {

        private View itemView;
        private ImageView imageView;

        BannerSliderAdapterVH(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            this.itemView = itemView;
        }

    }

    public interface BannerItemClickListener {
        void mBannerItemClickListener(String bannerId);
    }
}
