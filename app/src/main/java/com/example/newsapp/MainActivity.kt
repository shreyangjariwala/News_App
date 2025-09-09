package com.example.newswave

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.android.synthetic.main.activity_main.* // or use findViewById

class MainActivity : AppCompatActivity() {

    private var interstitialAd: InterstitialAd? = null
    private var appOpenAd: AppOpenAd? = null
    private var rewardedAd: RewardedAd? = null
    private var nativeAd: NativeAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        rvNews.layoutManager = LinearLayoutManager(this)
        val dummy = NewsRepository.getDummyNews(this)
        rvNews.adapter = NewsAdapter(dummy)


        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)


        findViewById<Button>(R.id.btnBanner).setOnClickListener {

            adView.loadAd(AdRequest.Builder().build())
        }


        InterstitialAd.load(this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) { interstitialAd = ad }
            })
        findViewById<Button>(R.id.btnInterstitial).setOnClickListener {
            interstitialAd?.show(this) ?: run {

                InterstitialAd.load(this,
                    "ca-app-pub-3940256099942544/1033173712",
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(ad: InterstitialAd) { interstitialAd = ad; ad.show(this@MainActivity) }
                    })
            }
        }


        AppOpenAd.load(this, "ca-app-pub-3940256099942544/3419835294", adRequest, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) { appOpenAd = ad }
            })
        findViewById<Button>(R.id.btnAppOpen).setOnClickListener {
            appOpenAd?.show(this)
        }


        val builder = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
        builder.forNativeAd { ad ->
            nativeAd?.destroy()
            nativeAd = ad

        }
        builder.withNativeAdOptions(NativeAdOptions.Builder().build())
        val adLoader = builder.withAdListener(object : AdListener() {}).build()
        adLoader.loadAd(adRequest)
        findViewById<Button>(R.id.btnNative).setOnClickListener {
            nativeAd?.let {
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle(it.headline)
                    .setMessage(it.body)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
        }

        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) { rewardedAd = ad }
        })
        findViewById<Button>(R.id.btnRewarded).setOnClickListener {
            rewardedAd?.show(this) { reward: RewardItem ->
                // reward.amount / reward.type
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setMessage(getString(R.string.reward_received, reward.amount))
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            } ?: run {
                RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917", AdRequest.Builder().build(),
                    object : RewardedAdLoadCallback() {
                        override fun onAdLoaded(ad: RewardedAd) { rewardedAd = ad; rewardedAd?.show(this@MainActivity) {} }
                    })
            }
        }

    }

    override fun onDestroy() {
        nativeAd?.destroy()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == R.id.menu_privacy) {
            val url = "https://YOUR_BLOGGER_URL_HERE"
            startActivity(Intent(this, PrivacyWebViewActivity::class.java).apply {
                putExtra("url", url)
            })
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}


