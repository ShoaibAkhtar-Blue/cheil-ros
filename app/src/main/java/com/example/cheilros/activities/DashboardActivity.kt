package com.example.cheilros.activities

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.cheilros.R
import com.example.cheilros.adapters.MenuNavigationAdapter
import com.example.cheilros.data.AppSetting
import com.example.cheilros.datavm.AppSettingViewModel
import com.example.cheilros.models.MenuNavigationModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var mAppSettingViewModel: AppSettingViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration

    var gridView: GridView? = null
    var menuData: ArrayList<MenuNavigationModel>? = null
    var adapter: MenuNavigationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        mAppSettingViewModel = ViewModelProvider(this).get(AppSettingViewModel::class.java)
        val settingData:List<AppSetting> = mAppSettingViewModel.getAllSetting



        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.setScrimColor(Color.TRANSPARENT)
        val toggle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navView: NavigationView = findViewById(R.id.nav_view)

        val width = resources.displayMetrics.widthPixels
        val params = nav_view.layoutParams as DrawerLayout.LayoutParams
        params.width = width
        nav_view.layoutParams = params

        /*val menus = arrayOf("Journey Plan", "My Coverage", "Photo", "Video", "Friends", "Messages", "Profile", "Setting")
        val icon = intArrayOf(
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery)
        val iconSelected = intArrayOf(
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery)
        menuData = ArrayList<MenuNavigationModel>()
        for (i in menus.indices) {
            val menu = MenuNavigationModel()
            menu.menuName = menus[i]
            menu.menuIcon = icon[i]
            menuData!!.add(menu)
        }*/

        menuData = ArrayList<MenuNavigationModel>()
        var menuDataList:List<AppSetting> = emptyList()
        try {
            menuDataList =settingData.filter { it.screenName == "Menu"}
            println(menuDataList)
            for (m in menuDataList) {
                val menu = MenuNavigationModel()
                menu.menuName = m.labelName
                menu.menuImage = m.imagePath
                menuData!!.add(menu)
            }
        }catch (ex: Exception){

        }

        gridView = findViewById(R.id.gridview) as GridView
        adapter = MenuNavigationAdapter(this, menuData!!)
        gridView!!.adapter = adapter

        gridView!!.onItemClickListener = OnItemClickListener { parent, v, i, id ->
            //Toast.makeText(this, "menu " + menuDataList.get(i).fixedLabelName + " clicked! $i", Toast.LENGTH_SHORT).show()
            val navController = findNavController(R.id.main_nav_fragment)
            try {
                if(menuDataList.get(i).fixedLabelName == "MenuTitle3")
                    findNavController(R.id.main_nav_fragment).navigate(R.id.action_dashboardFragment_to_journeyPlanFragment)
//                    navController.navigate(
//                            R.id.action_dashboardFragment_to_journeyPlanFragment ,
//                            NavOptions.Builder().setPopUpTo(
//                                    R.id.dashboardFragment,
//                                    false)
//                    )
                    //findNavController(R.id.main_nav_fragment).navigate(R.id.action_dashboardFragment_to_journeyPlanFragment)

                if(menuDataList.get(i).fixedLabelName == "MenuTitle2")
                    findNavController(R.id.main_nav_fragment).navigate(R.id.action_dashboardFragment_to_myCoverageFragment)
            }catch (e: Exception){
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                Log.e("Error_Nav", e.message.toString())
            }



            val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
            drawer.closeDrawer(GravityCompat.START)

            /*val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
            drawer.closeDrawer(GravityCompat.START)
            menuData!!.clear()
            for (j in menus.indices) {
                val menu = MenuNavigation17Model()
                menu.setMenuName(menus[j])
                menu.setMenuIcon(icon[j])
                if (i == j) {
                    menu.setSelected(true)
                    menu.setMenuIcon(iconSelected[j])
                }
                menuData!!.add(menu)
            }
            adapter!!.notifyDataSetChanged()*/
        }

        /*val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
                ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)*/
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.dashboard, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_nav_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}