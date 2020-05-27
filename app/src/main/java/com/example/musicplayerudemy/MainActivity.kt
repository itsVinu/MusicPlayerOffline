package com.example.musicplayerudemy

import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.song_ticket.*
import kotlinx.android.synthetic.main.song_ticket.view.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    var listSongs = ArrayList<SongInfo>()
    var songadapter:MySongAdapter? = null
    var mp:MediaPlayer? = null
    var myListSong = ArrayList<SongInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        LoadURLOnLine()
        CheckUserPermission()


        var myTracking = mySongTrack()
        myTracking.start()
    }

    fun LoadURLOnLine(){
        listSongs.add(SongInfo("abcd","ABCD","http://http://server6.mp3quran.net/thubti/001.mp3"))
        listSongs.add(SongInfo("efgh","EFGH","http://http://server6.mp3quran.net/thubti/002.mp3"))
        listSongs.add(SongInfo("ijkl","IJKL","http://http://server6.mp3quran.net/thubti/003.mp3"))
        listSongs.add(SongInfo("mnop","MNOP","http://http://server6.mp3quran.net/thubti/004.mp3"))
        listSongs.add(SongInfo("qrst","QRST","http://http://server6.mp3quran.net/thubti/005.mp3"))
    }

    inner class MySongAdapter: BaseAdapter {

        var myListSong = ArrayList<SongInfo>()
        constructor( myListSong:ArrayList<SongInfo>):super() {
            this.myListSong = myListSong
        }


        override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
                val myView = layoutInflater.inflate(R.layout.song_ticket, null)
            val Song = this.myListSong[position]
            myView.songName.text = Song.Title
            myView.author.text = Song.AuthorName

            myView.btnPlay.setOnClickListener {

                Toast.makeText(this@MainActivity,"button clicked",Toast.LENGTH_SHORT).show()
                if (myView.btnPlay.text == "Stop"){
                    mp!!.stop()
                    myView.btnPlay.text = "Play"
                } else {
                    mp = MediaPlayer()

                    try {
                        mp!!.setDataSource(Song.SongURL)
                        mp!!.prepare()
                        mp!!.start()
                        myView.btnPlay.text = "Stop"
                        seekBar.max = mp!!.duration
                    } catch (ex: Exception) {

                    }
                }
            }
            return myView
        }

        override fun getItem(item: Int): Any {
            return this.myListSong[item]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return this.myListSong.size
        }

    }

    inner class mySongTrack(): Thread(){

        override fun run() {

            while (true){
                try {
                    Thread.sleep(1000)
                }
                catch (ex:Exception){ }

                runOnUiThread {

                    if (mp!= null){
                        seekBar.progress = mp!!.currentPosition
                    }
                }
            }
        }
    }

    fun CheckUserPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf( android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_ASK_PERMISSIONS)
                return
            }
        }
        LoadSong()
    }

    // get access to location permission
    private val REQUEST_CODE_ASK_PERMISSIONS = 123

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when( requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> if ( grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LoadSong()
            } else {
                //Permission Denied
                Toast.makeText(this,"",Toast.LENGTH_SHORT).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun LoadSong() {
        val allSongsURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val cursor = contentResolver.query(allSongsURI, null, selection, null, null)

        if (cursor!= null) {
            if (cursor!!.moveToFirst()) {

                do {

                    val songURL = cursor!!.getString( cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val SongAuthor = cursor!!.getString( cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val SongName = cursor!!.getString( cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))

                    listSongs.add( SongInfo(SongName, SongAuthor, songURL))

                } while ( cursor!!.moveToNext())
            }
            cursor!!.close()

            songadapter = MySongAdapter(listSongs)
            listView.adapter = songadapter

        }
    }
}
