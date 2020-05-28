package com.designbyark.layao.adapters

import android.graphics.Paint
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.designbyark.layao.common.circularProgressBar
import com.designbyark.layao.common.findDiscountPrice
import com.designbyark.layao.common.getSavingPrice
import com.designbyark.layao.data.Products
import com.designbyark.layao.data.cart.Cart
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

@BindingAdapter("app:setDiscount")
fun setDiscount(view: TextView, discount: Double) {
    view.text = String.format(Locale.getDefault(), "%.0f%% off", discount)
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

@BindingAdapter("app:setUnitLabel")
fun setUnitLabel(view: TextView, unit: String) {
    view.text = String.format("Per %s", unit)
}

