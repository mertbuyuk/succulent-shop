package school.cactus.succulentshop.signup.validation

import school.cactus.succulentshop.R
import school.cactus.succulentshop.validation.Validator

class IdentifierValidator : Validator {

    override fun validate(field: String) = when {
        field.isEmpty() -> R.string.email_required
        field.length < 5 || field.length > 50 -> R.string.invalid_email
        (!field.contains("@") || !field.contains(".")) -> R.string.invalid_email
        else -> null
    }
}
