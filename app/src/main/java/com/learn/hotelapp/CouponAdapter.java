package com.learn.hotelapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {

    private static List<item_coupon> couponList;

    public CouponAdapter(List<item_coupon> couponList) {
        CouponAdapter.couponList = couponList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_coupon_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponAdapter.ViewHolder holder, int position) {
        item_coupon coupon = couponList.get(position);
        holder.imgCoupon.setImageResource(coupon.getItemImg());
        holder.tvCouponDiscount.setText(coupon.getItemDiscount() + "% OFF Discount");
        holder.tvCouponTitle.setText(coupon.getItemTitle());
        holder.tvCouponDate.setText(coupon.getItemDate());

        // Next button
//        holder.btn_ad_next.setOnClickListener(view -> {
//            Intent intent = new Intent(view.getContext(), launchPage.class);
//            view.getContext().startActivity(intent);
//        });
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCoupon;
        TextView tvCouponDiscount, tvCouponTitle, tvCouponDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCoupon = itemView.findViewById(R.id.img_coupon);
            tvCouponDiscount = itemView.findViewById(R.id.tv_coupon_discount);
            tvCouponTitle = itemView.findViewById(R.id.tv_coupon_title);
            tvCouponDate = itemView.findViewById(R.id.tv_coupon_date);
        }
    }

    public void removeItem(int position) {
        couponList.remove(position);
        notifyItemRemoved(position);
    }
}
