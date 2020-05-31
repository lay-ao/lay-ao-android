package com.designbyark.layao.adapters

import android.graphics.Paint
import android.text.format.DateUtils
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.BindingAdapter
import com.algolia.instantsearch.helper.android.highlighting.toSpannedString
import com.bumptech.glide.Glide
import com.designbyark.layao.common.*
import com.designbyark.layao.data.Order
import com.designbyark.layao.data.Products
import com.designbyark.layao.data.ProductsData
import com.designbyark.layao.data.cart.Cart
import com.google.android.material.card.MaterialCardView
import com.google.firebase.Timestamp
import java.util.*

@BindingAdapter("app:setImage")
fun setImage(view: ImageView, source: String) {
    Glide.with(view.context)
        .load(source)
        .placeholder(circularProgressBar(view.context))
        .into(view)
}

@BindingAdapter("app:setTitle")
fun setTitle(view: TextView, title: String) {
    view.text = title
}

@BindingAdapter("app:setValidity")
fun setValidity(view: TextView, date: Date) {
    view.text = String.format("Valid till %s", formatDate(date))
}

@BindingAdapter("app:setPromoCode")
fun setPromoCode(view: TextView, code: String) {
    view.text = String.format("Promo Code: %s", code)
}

@BindingAdapter("app:setDiscount")
fun setDiscount(view: TextView, discount: Double) {
    if (discount > 0) {
        view.text = String.format(Locale.getDefault(), "%.0f%% off", discount)
    } else {
        view.visibility = View.INVISIBLE
    }
}

@BindingAdapter("app:setPrice")
fun setPrice(view: TextView, product: Products) {
    if (product.discount > 0) {
        view.text = String.format(
            Locale.getDefault(),
            "Rs. %.0f/%s",
            findDiscountPrice(product.price, product.discount),
            product.unit
        )
    } else {
        view.text = String.format(
            Locale.getDefault(), "Rs. %.0f/%s",
            product.price, product.unit
        )
    }
}

@BindingAdapter("app:setSavingPrice")
fun setSavingPrice(view: TextView, product: Products) {
    view.text = String.format(
        "Save Rs. %.0f",
        getSavingPrice(findDiscountPrice(product.price, product.discount), product.price)
    )
}

@BindingAdapter("app:setDiscountAmount")
fun setDiscountAmount(view: TextView, discount: Double) {
    if (discount > 0) {
        view.text = String.format(Locale.getDefault(), "%.0f%% discount applied", discount)
    } else {
        view.visibility = View.INVISIBLE
    }
}

//@BindingAdapter("app:hideDiscount")
//fun hideDiscount(view: TextView, discount: Double) {
//    if (discount > 0) {
//        view.visibility = View.VISIBLE
//    } else {
//        view.visibility = View.INVISIBLE
//    }
//}

@BindingAdapter("app:hideDecrease")
fun hideDecrease(view: ImageButton, quantity: Long) {
    if (quantity > 1) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.INVISIBLE
    }
}

@BindingAdapter("app:hideIncrease")
fun hideIncrease(view: ImageButton, cart: Cart) {
    if (cart.quantity == cart.stock) {
        view.visibility = View.INVISIBLE
    } else {
        view.visibility = View.VISIBLE
    }
}

@BindingAdapter("app:setPerPrice")
fun setPerPrice(view: TextView, cart: Cart) {
    view.text = String.format(Locale.getDefault(), "Rs. %.0f/%s", cart.price, cart.unit)
    if (cart.discount > 0) {
        view.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
    }
}

@BindingAdapter("app:setDiscountPrice")
fun setDiscountPrice(view: TextView, cart: Cart) {
    if (cart.discount > 0) {
        view.text = String.format(
            Locale.getDefault(), "Rs. %.0f/%s",
            findDiscountPrice(cart.price, cart.discount), cart.unit
        )
    } else {
        view.visibility = View.INVISIBLE
    }
}

@BindingAdapter("app:setQuantity")
fun setQuantity(view: TextView, cart: Cart) {
    if (cart.discount > 0) {
        // When more than one item
        if (cart.quantity > 1) {
            view.text = String.format(
                Locale.getDefault(),
                "Rs. %.0f / %d %ss",
                (findDiscountPrice(cart.price, cart.discount) * cart.quantity),
                cart.quantity,
                cart.unit
            )
        } else { // When a single item
            view.text = String.format(
                Locale.getDefault(),
                "Rs. %.0f / %s",
                (findDiscountPrice(cart.price, cart.discount) * cart.quantity),
                cart.unit
            )
        }
    } else {
        if (cart.quantity > 1) {
            view.text = String.format(
                Locale.getDefault(),
                "Rs. %.0f / %d %ss", (cart.price * cart.quantity), cart.quantity, cart.unit
            )
        } else {
            view.text = String.format(
                Locale.getDefault(),
                "Rs. %.0f / %ss", (cart.price * cart.quantity), cart.unit
            )
        }
    }
}

@BindingAdapter("app:setSubTotal")
fun setSubtotal(view: TextView, cart: Cart) {
    if (cart.discount > 0) {
        view.text = String.format(
            Locale.getDefault(),
            "Rs. %.2f", findDiscountPrice(cart.price, cart.discount) * cart.quantity
        )
    } else {
        view.text = String.format(
            Locale.getDefault(),
            "Rs. %.2f", cart.price * cart.quantity
        )
    }
}

@ExperimentalStdlibApi
@BindingAdapter("app:setChangingQuantity")
fun setChangingQuantity(view: TextView, cart: Cart) {
    if (cart.quantity > 1) {
        view.text = String.format(
            Locale.getDefault(),
            "%d %ss", cart.quantity, cart.unit.capitalize(Locale.getDefault())
        )
    } else {
        view.text = String.format(
            Locale.getDefault(),
            "%d %s",
            cart.quantity, cart.unit.capitalize(Locale.getDefault())
        )
    }
}

//@BindingAdapter("app:setUnitLabel")
//fun setUnitLabel(view: TextView, unit: String) {
//    view.text = String.format("Per %s", unit)
//}

@BindingAdapter("app:setSearchProductTitle")
fun setSearchProductTitle(view: TextView, productsData: ProductsData) {
    view.text = productsData.highlightedTitle?.toSpannedString() ?: productsData.title
}

@BindingAdapter("app:setOrderStatus")
fun setOrderStatus(view: TextView, status: Int) {
    view.text = getOrderStatus(status)
}

@BindingAdapter("app:setOrderUI")
fun setOrderUI(view: MaterialCardView, status: Int) {
    when (status) {
        0 -> view.strokeColor = getColor(view.context, android.R.color.holo_orange_dark)
        1 -> view.strokeColor = getColor(view.context, android.R.color.holo_blue_dark)
        2 -> view.strokeColor = getColor(view.context, android.R.color.holo_green_dark)
        3 -> view.strokeColor = getColor(view.context, android.R.color.holo_purple)
        4 -> view.strokeColor = getColor(view.context, android.R.color.holo_red_dark)
        5 -> view.strokeColor = getColor(view.context, android.R.color.holo_green_dark)
        6 -> view.strokeColor = getColor(view.context, android.R.color.holo_red_dark)
        else -> view.strokeColor = getColor(view.context, android.R.color.black)
    }
}

@BindingAdapter("app:setGrandTotal")
fun setGrandTotal(view: TextView, grandTotal: Double) {
    view.text = String.format(Locale.getDefault(), "Grand Total: Rs. %.0f", grandTotal)
}

@BindingAdapter("app:setOrderTiming")
fun setOrderTiming(view: TextView, time: Timestamp) {
    view.text = String.format(
        "Order time: %s", DateUtils.getRelativeDateTimeString(
            view.context,
            time.toDate().time,
            DateUtils.SECOND_IN_MILLIS,
            DateUtils.YEAR_IN_MILLIS,
            0
        )
    )
}

@BindingAdapter("app:setItems")
fun setItems(view: TextView, count: Int) {
    var plurals = "item"
    if (count > 1) {
        plurals = "items"
    }
    view.text = String.format("Cart item(s): %d %s", count, plurals)
}

@BindingAdapter("app:setOrderId")
fun setOrderId(view: TextView, order: Order) {
    view.text = String.format(
        "Order #%s",
        formatOrderId(order.orderId, order.contactNumber).toUpperCase(Locale.getDefault())
    )
}
