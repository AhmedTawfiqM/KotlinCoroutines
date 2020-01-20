package com.atdev.kotlincorotinues

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.btn_StartJob
import kotlinx.android.synthetic.main.activity_main.textView2
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private val RESULT1 = "Result 1"
    private val RESULT2 = "Result 2"

    //............
    private lateinit var job: CompletableJob
    private val PROGRESSMAX = 100
    private val PROGRESS_START = 0
    private val JobTime = 4000 //ms
    //.............

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        button.setOnClickListener {
//
//            //IO > for input Output Processing > network or local database interaction
//            // ...MAIN.... > UI Main THread
//            // DEFAUlt > for  More heav computational Work
//            CoroutineScope(IO).launch {
//
//                fakeApiRequest()
//
//            }
//
//        }
        //
        //Second Part of Kotlin Coroutines > Jobs
        btn_StartJob.text = "Start Job"
        btn_StartJob.setOnClickListener {

            if (!::job.isInitialized) {
                initJob()
            }
            progressBar.startOrcancelJob(job)
        }

    }//

    private fun ProgressBar.startOrcancelJob(job: Job) {

        if (this.progress > 0) {

            println(" $job is Already Active. CAncelling.....")
            resetJob()

        } else {
            btn_StartJob.text = "Cancel Job"

            CoroutineScope(IO + job).launch {
                //IO + JOb > because when cancel job only not cancel all Entire IO Jobs


                for (i in PROGRESS_START..PROGRESSMAX) {
                    delay((JobTime / PROGRESSMAX).toLong())
                    this@startOrcancelJob.progress = i
                }

                updateCompleteTextonMAInThread("job is Comlete")
            }
        }

    }

    private fun updateCompleteTextonMAInThread(text: String) {

        CoroutineScope(Main).launch {
            textView2.text = text
        }
    }

    private fun resetJob() {

        if (job.isActive || job.isCompleted) {
            job.cancel(CancellationException("Resetting Job..."))
        }
        initJob()
    }

    private fun initJob() {

        btn_StartJob.text = "Start Job"
        updateCompleteTextonMAInThread("")

        job = Job()
        job.invokeOnCompletion {
            //Check if it is null by ?
            it?.message.let {

                var msg = it
                if (msg.isNullOrBlank()) {
                    msg = "UnKnowm=n Cancwllation Error"
                }
                println("Job: $job was cancelled . Reason : $msg")
                showToast("Job: $job was cancelled . Reason : $msg")
            }
        }

        progressBar.max = PROGRESSMAX
        progressBar.progress = PROGRESS_START

    }

    private fun showToast(text: String) {

        GlobalScope.launch(Main) {
            Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()

        }
    }

    private fun setText(input: String) {

        val newText = textView.text.toString() + "\n $input"
        textView.text = newText

    }

    private suspend fun setTextonMainThread(input: String) {
        withContext(Main) {
            setText(input)
        }
    }

    private suspend fun fakeApiRequest() {

        val res1 = getFirstRequest1APi()
        setTextonMainThread(res1)

        val res2 = get2Request2API()
        setTextonMainThread(res2)
    }

    // Suspend for Kotlin Coroutines
    private suspend fun getFirstRequest1APi(): String {

        Utils.LogThread("getFirstRequest1APi")
        delay(1000) //delay Coroutines only not thread // coroutines NOT threads
        return RESULT1
    }

    private suspend fun get2Request2API(): String {

        Utils.LogThread("get2Request2API")
        delay(1000)
        return RESULT2
    }

}
