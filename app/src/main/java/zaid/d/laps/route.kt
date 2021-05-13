package zaid.d.laps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import zaid.d.laps.ui.main.RouteFragment

class route : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.route_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RouteFragment.newInstance())
                .commitNow()
        }
    }
}