package laundry.driveraslijempol.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.util.LruCache;

/**
 * Created by Bagus on 24/07/2018.
 */
public class MySingleton {
    private static MySingleton mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    //For Order
    private String tanggalOrder;
    private String jamOrder;
    private String addressOrder;
    private String[] pakaianOrder;
    private String[] tipepakaianOrder;
    private Integer[] hargapakaianOrder;
    private Integer[] jmlpakaianOrder;
    private String tipeService;
    private String noteOrder;
    private Integer tipeServiceHarga;
    private String voucher;
    private Integer nominal;

    public void resetOrder(){
        this.tanggalOrder = null;
        this.jamOrder = null;
        this.addressOrder = null;
        this.pakaianOrder = null;
        this.tipepakaianOrder = null;
        this.hargapakaianOrder = null;
        this.jmlpakaianOrder = null;
        this.tipeService = null;
        this.noteOrder = null;
        this.tipeServiceHarga = null;
        this.voucher = null;
        this.nominal = null;
    }

    private MySingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized MySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        if (req.getRetryPolicy() == null){
            req.setRetryPolicy(new DefaultRetryPolicy(
            15000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    //For Order
    public String getNoteOrder() {
        return this.noteOrder;
    }

    public void setNoteOrder(String noteOrder) {
        this.noteOrder = noteOrder;
    }

    public void setTanggalOrder(String tanggalOrder){
        this.tanggalOrder = tanggalOrder;
    }

    public String getTanggalOrder() {
        return this.tanggalOrder;
    }

    public void setJamOrder(String jamOrder){
        this.jamOrder = jamOrder;
    }

    public String getJamOrder() {
        return this.jamOrder;
    }

    public void setAddressOrder(String addressOrder){
        this.addressOrder = addressOrder;
    }

    public String getAddressOrder() {
        return this.addressOrder;
    }

    public void setPakaianOrder(String[] pakaianOrder){
        this.pakaianOrder = pakaianOrder;
    }

    public String[] getPakaianOrder() {
        return this.pakaianOrder;
    }

    public void setJmlpakaianOrder(Integer[] jmlpakaianOrder){
        this.jmlpakaianOrder = jmlpakaianOrder;
    }

    public void setJmlpakaianOrderByPos(Integer pos, Integer value){
        this.jmlpakaianOrder[pos] = value;
    }

    public Integer[] getJmlpakaianOrder() {
        return this.jmlpakaianOrder;
    }

    public void setTipeService(String tipeService) {
        this.tipeService = tipeService;
    }

    public String getTipeService() {
        return tipeService;
    }

    public String[] getTipepakaianOrder() {
        return this.tipepakaianOrder;
    }

    public void setTipepakaianOrder(String[] tipepakaianOrder) {
        this.tipepakaianOrder = tipepakaianOrder;
    }

    public Integer[] getHargapakaianOrder() {
        return this.hargapakaianOrder;
    }

    public void setHargapakaianOrder(Integer[] hargapakaianOrder) {
        this.hargapakaianOrder = hargapakaianOrder;
    }

    public Integer getTipeServiceHarga() {
        return this.tipeServiceHarga;
    }

    public void setTipeServiceHarga(Integer tipeServiceHarga) {
        this.tipeServiceHarga = tipeServiceHarga;
    }

    public String getVoucher() {
        return this.voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public Integer getNominal() {
        return this.nominal;
    }

    public void setNominal(Integer nominal) {
        this.nominal = nominal;
    }
}
