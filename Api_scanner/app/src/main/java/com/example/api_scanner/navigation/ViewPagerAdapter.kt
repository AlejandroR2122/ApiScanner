import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.api_scanner.fragments.HomeFragment
import com.example.api_scanner.fragments.FragmentGestionAlmacen
import com.example.api_scanner.fragments.FragmentDetailsPrinter
import com.example.api_scanner.fragments.FragmentSettings

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4 // Número de fragmentos

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> FragmentDetailsPrinter()
            2 -> FragmentGestionAlmacen()
            3 -> FragmentSettings()
           
            else -> throw IllegalStateException("Invalid position")
        }
    }
}
