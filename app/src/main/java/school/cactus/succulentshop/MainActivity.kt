package school.cactus.succulentshop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import school.cactus.succulentshop.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SucculentShop)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
