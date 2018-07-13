package com.example.karshsoni.calendarapidemo

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    val EVENT_PROJECTION = arrayOf(CalendarContract.Calendars._ID, // 0
            CalendarContract.Calendars.ACCOUNT_NAME, // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    )

    // The indices for the projection array above.
    val PROJECTION_ID_INDEX = 0
    val PROJECTION_ACCOUNT_NAME_INDEX = 1
    val PROJECTION_DISPLAY_NAME_INDEX = 2
    val PROJECTION_OWNER_ACCOUNT_INDEX = 3

    val permissions = arrayOf(android.Manifest.permission.READ_CALENDAR, android.Manifest.permission.WRITE_CALENDAR)
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val checkPermissionRead = ContextCompat.checkSelfPermission(this, permissions[0])
        val checkPermissionWrite = ContextCompat.checkSelfPermission(this, permissions[1])

        if (checkPermissionRead != PackageManager.PERMISSION_GRANTED && checkPermissionWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 0)
        } else {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
        }

        btnQuery.setOnClickListener {
            var cur: Cursor? = null
            val cr = contentResolver
            val uri = CalendarContract.Calendars.CONTENT_URI
            val selection = ("((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                    + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?)AND (\"\n" +
                    "//            + CalendarContract.Calendars.OWNER_ACCOUNT + \" = ?))")

            // AND ("
//            + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?)
            val selectionArgs = arrayOf("sonirashmi822@gmail.com", "com.example", "sonirashmi822@gmail.com")
// Submit the query and get a Cursor object back.
            cur = cr.query(uri, EVENT_PROJECTION, null, null, null)
//            cur.moveToFirst()
            Log.d(TAG, "Cursor: $cur, $cr");
            while (cur.moveToNext()) {
                // Get the field values
                var calID: Long? = cur.getLong(PROJECTION_ID_INDEX);
                var displayName: String? = cur.getString(PROJECTION_DISPLAY_NAME_INDEX)
                var accountName: String? = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX)
                var ownerName: String? = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

                Log.d(TAG, "calID: $calID, displayName: $displayName, accountName: $accountName, ownerName: $ownerName")
            }
            cur.close()
        }

        btnModify.setOnClickListener {
            val calID: Long = 4
            val values = ContentValues()
            // The new display name for the calendar
            values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "Karsh's Calendar")
            val updateUri = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, calID)
            val rows = contentResolver.update(updateUri, values, null, null)
            Log.i(TAG, "Rows updated: " + rows)
        }

        btnInsert.setOnClickListener {
            var calID:Long = 3;
            var startMillis:Long = 0;
            var endMillis:Long = 0;
            var beginTime: Calendar = Calendar.getInstance();
            beginTime.set(2018, 9, 14, 7, 30);
            startMillis = beginTime.getTimeInMillis();
            var endTime:Calendar = Calendar.getInstance();
            endTime.set(2018, 9, 14, 8, 45);
            endMillis = endTime.getTimeInMillis();
            var cr: ContentResolver = getContentResolver();
            var values:ContentValues = ContentValues();
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.TITLE, "Jazzercise");
            values.put(CalendarContract.Events.DESCRIPTION, "Group workout");
            values.put(CalendarContract.Events.CALENDAR_ID, calID);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
            var uri: Uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            val eventID = java.lang.Long.parseLong(uri.lastPathSegment)

            Log.d(TAG, "event Id: $eventID");

            // get the event ID that is the last element in the Uri
//            var eventID: Long = Long.parseLong(uri.getLastPathSegment());
        }

        btnModifyEvent.setOnClickListener {
            val eventID: Long = 159

            val cr = contentResolver
            val values = ContentValues()
            var updateUri: Uri? = null
// The new title for the event
            values.put(CalendarContract.Events.TITLE, "Kickboxing")
            updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID)
            val rows = contentResolver.update(updateUri!!, values, null, null)
            Log.i(TAG, "Rows updated: " + rows)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {

            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(applicationContext, "Permissions Granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, " Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}
