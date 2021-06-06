package school.cactus.succulentshop

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import school.cactus.succulentshop.product.ProductItem
import school.cactus.succulentshop.product.detail.RelatedProductDecoration
import school.cactus.succulentshop.product.detail.related.RelatedProductAdapter
import school.cactus.succulentshop.product.list.ProductAdapter
import school.cactus.succulentshop.product.list.ProductDecoration

@BindingAdapter("error")
fun TextInputLayout.error(errorMessage: Int?) {
    error = errorMessage?.resolveAsString(context)
    isErrorEnabled = errorMessage != null
}

val productAdapter = ProductAdapter()

@BindingAdapter("app:products", "app:itemClickListener")
fun RecyclerView.products(
    productItems: List<ProductItem>?,
    itemClickListener: (ProductItem) -> Unit
) {
    adapter = productAdapter
    productAdapter.itemClickListener = itemClickListener

    if (itemDecorationCount == 0) {
        addItemDecoration(ProductDecoration())
    }

    productAdapter.submitList(productItems.orEmpty())
}

val relatedProductAdapter = RelatedProductAdapter()

@BindingAdapter("app:relatedProducts", "app:relatedItemClickListener")
fun RecyclerView.relatedProducts(
    productItems: List<ProductItem>?,
    itemClickListener: (ProductItem) -> Unit
) {
    adapter = relatedProductAdapter
    relatedProductAdapter.itemClickListener = itemClickListener

    if (itemDecorationCount == 0) {
        addItemDecoration(RelatedProductDecoration())
    }

    relatedProductAdapter.submitList(productItems.orEmpty())
}

@BindingAdapter("app:imageUrl")
fun ImageView.imageUrl(imageUrl: String?) {
    imageUrl?.let {
        Glide.with(this)
            .load(imageUrl)
            .centerInside()
            .into(this)
    }
}

@BindingAdapter("app:isEmpty")
fun TextView.isEmpty(productItems: List<ProductItem>?) {
    if (productItems?.isEmpty() == true) {
        this.visibility = View.GONE
    }
}
