package laundry.driveraslijempol.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import laundry.driveraslijempol.R;
import laundry.driveraslijempol.fragment.DeliveryFragment;
import laundry.driveraslijempol.fragment.HomeFragment;
import laundry.driveraslijempol.fragment.PickupFragment;

public class MainActivity extends AppCompatActivity {

    boolean doubleback = false;
    Fragment lastFragment;

    private BottomNavigationView.OnNavigationItemReselectedListener onNavigationItemReselectedListener = new BottomNavigationView.OnNavigationItemReselectedListener() {
        @Override
        public void onNavigationItemReselected(@NonNull MenuItem item) {

        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    lastFragment = fragment;
                    return true;
                case R.id.navigation_pickup:
                    fragment = new PickupFragment();
                    loadFragment(fragment);
                    lastFragment = fragment;
                    return true;
                case R.id.navigation_delivery:
                    fragment = new DeliveryFragment();
                    loadFragment(fragment);
                    lastFragment = fragment;
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.main_navigation);
        Fragment fragment = new HomeFragment();
        loadFragment(fragment);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setOnNavigationItemReselectedListener(onNavigationItemReselectedListener);
    }

    @Override
    public void onBackPressed() {
        if(doubleback){
            finish();
        }else{
            this.doubleback = true;
            Toast.makeText(this, "Tekan back sekali lagi untuk keluar aplikasi!", Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleback = false;
            }
        }, 2500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lastFragment != null)
        loadFragment(lastFragment);
    }
}
