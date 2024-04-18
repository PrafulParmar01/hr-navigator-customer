package com.hr.navigator.customer.ui.companylist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.hr.navigator.customer.R
import com.hr.navigator.customer.ui.profile.CompanyModel

class CompanyListAdapter(val mContext: Context) :
    RecyclerView.Adapter<CompanyListAdapter.ViewHolder>() {

    private var mList: List<CompanyModel> = arrayListOf()
    private var selectedPosition = -1

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val txtCompanyName: AppCompatTextView = itemView.findViewById(R.id.txtCompanyName)
        val txtAddressLine: AppCompatTextView = itemView.findViewById(R.id.txtAddressLine)
        val txtEmail: AppCompatTextView = itemView.findViewById(R.id.txtEmail)
        val tvPhone: AppCompatTextView = itemView.findViewById(R.id.txtPhone)
        val btnCheck: AppCompatImageView = itemView.findViewById(R.id.btnCheck)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = mList[position]
        holder.txtCompanyName.text = info.companyName
        holder.txtAddressLine.text = "Address: " + info.addressLine
        holder.txtEmail.text = "E-mail: " + info.email
        holder.tvPhone.text = "Phone: " + info.phone

        if (selectedPosition == holder.adapterPosition) {
            holder.btnCheck.setImageResource(R.drawable.ic_checked)
            onClickArrow(mList[holder.adapterPosition])
        } else {
            holder.btnCheck.setImageResource(R.drawable.ic_unchecked)
        }

        holder.itemView.setOnClickListener {
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_company_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun onUpdateDiaryList(diaryList: List<CompanyModel>) {
        mList = diaryList
        notifyDataSetChanged()
    }

    var onClickArrow: ((data: CompanyModel) -> Unit) = { }

}