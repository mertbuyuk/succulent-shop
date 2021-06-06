package school.cactus.succulentshop.signup.validation

import school.cactus.succulentshop.R
import school.cactus.succulentshop.validation.Validator

class PasswordValidator : Validator {
    override fun validate(field: String) = when {
        field.isEmpty() -> R.string.password_cannot_be_empty
        field.length < 7 -> R.string.password_short
        field.length > 40 -> R.string.password_long
        !field.any { it.isLowerCase() } -> R.string.password_rules
        !field.any { it.isUpperCase() } -> R.string.password_rules
        !field.any { it.isDigit() } -> R.string.password_rules
        field.all { it.isLetterOrDigit() } -> R.string.password_rules

        else -> null
    }
}