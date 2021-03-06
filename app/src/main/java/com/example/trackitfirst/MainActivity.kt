package com.example.trackitfirst

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.trackitfirst.databinding.ActivityMainBinding


import java.util.Date
import java.io.File
import java.io.FileOutputStream
import android.widget.Toast
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import kotlin.properties.Delegates
import android.os.Environment





//  AdapterView.OnItemSelectedListener ,war auch irgendwie drine ?!
class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding

    private lateinit var spinnerL2 : Spinner
    private var spinnerL2position by Delegates.notNull<Int>()
    private lateinit var spinnerL3 : Spinner
    private var spinnerL3position by Delegates.notNull<Int>()

    var fileName = "TrackItData.csv"
    lateinit var loadedContent : String

    private lateinit var spinnerUnits : Spinner


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.custommenue,menu)


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.Import -> {

                var text = ""

                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val f = File(dir,"import.txt")
                if (f.exists()) {
                    // read from external file
                    var lines = f.readLines()
                    val size = lines.size
                    lines = lines.take(size)
                    text = lines.joinToString(System.lineSeparator())+"\n"

                    // write in internal file
                    val fnew = File(filesDir,fileName)
                    fnew.writeText(text)

                    Toast.makeText(this, "Import done", Toast.LENGTH_SHORT).show()
                }else
                {
                    Toast.makeText(this, "File import.txt could not loaded from Downloads directory", Toast.LENGTH_LONG).show()
                }
            };
            R.id.Export -> {

                load();

                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                //val dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                try{
                val f = File(dir,"export.txt")
                //f.bufferedWriter().use { out -> out.write(loadedContent) }
                f.writeText(loadedContent)
                }
                catch (e: Exception){
                    Toast.makeText(this, "exception "+e.toString(), Toast.LENGTH_LONG).show();

                }

                Toast.makeText(this, dir.toString()+"/export.txt", Toast.LENGTH_LONG).show();

            }
            R.id.share -> {

                load();

                val intent = Intent(Intent.ACTION_SEND)
                //intent.action = Intent.ACTION_SEND
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT,loadedContent)
                val chooser = Intent.createChooser(intent,"Share using ...")
                startActivity(chooser)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun load(){

        var fileInputStream:FileInputStream? = null
        fileInputStream = openFileInput(fileName)
        var inputStreamReader : InputStreamReader = InputStreamReader(fileInputStream)
        var bufferedReader: BufferedReader = BufferedReader(inputStreamReader)

        val stringBuilder:StringBuilder = StringBuilder()

        stringBuilder.append(bufferedReader.readText())
        fileInputStream.close()


        loadedContent= stringBuilder.toString()


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // src: https://www.youtube.com/watch?v=bd8WbZFV5IY
        //setContentView(R.layout.activity_main) // orginal




        val spinnerEvent : Spinner = findViewById(R.id.spinnerLayer1)
        spinnerL2 = findViewById(R.id.spinnerLayer2)
        spinnerL3 = findViewById(R.id.spinnerLayer3)
        spinnerUnits = findViewById(R.id.spinnerLayer4)
        spinnerL2position = 0
        spinnerL3position = 0


        binding.buttonAdd.setOnClickListener{

            val userInput=binding.ValueField.text.toString()

            if (userInput.contains(',')){
                Toast.makeText(this, " use . insead of , ", Toast.LENGTH_LONG).show();
            }else {

                val builder = StringBuilder()
                builder.clear()

                val date = Date(System.currentTimeMillis())
                builder.append("$date,")

                builder.append(binding.spinnerLayer1.selectedItem.toString())
                if (binding.spinnerLayer2.visibility == View.VISIBLE) {
                    builder.append(":" + binding.spinnerLayer2.selectedItem.toString())
                    if (binding.spinnerLayer3.visibility == View.VISIBLE) {
                        builder.append(":" + binding.spinnerLayer3.selectedItem.toString())
                        if (binding.spinnerLayer4.visibility == View.VISIBLE) {
                            builder.append(":" + binding.spinnerLayer4.selectedItem.toString())
                        }
                    }
                }


                if (userInput.isNotEmpty()) {
                    builder.append(",$userInput")
                }


                builder.append("\n")
                val data = builder.toString()


                // write to file
                val fileOutputStream: FileOutputStream =
                    openFileOutput(fileName, Context.MODE_APPEND)  // std is Context.MODE_PRIVATE
                fileOutputStream.write(data.toByteArray())
                fileOutputStream.close()

                binding.ValueField.text = null

                var dataDir: File = filesDir // https://developer.android.com/training/data-storage/app-specific
                Toast.makeText(this, "Saved to $dataDir/$fileName", Toast.LENGTH_LONG).show();
            }

        }


        binding.buttonLoad.setOnClickListener{

                var fileInputStream:FileInputStream? = null
                fileInputStream = openFileInput(fileName)
                var inputStreamReader : InputStreamReader = InputStreamReader(fileInputStream)
                var bufferedReader: BufferedReader = BufferedReader(inputStreamReader)

                val stringBuilder:StringBuilder = StringBuilder()

                stringBuilder.append(bufferedReader.readText())
                fileInputStream.close()

                //binding.editTextTextMultiLine2.setText(stringBuilder.toString())
                binding.textView2.text = stringBuilder.toString()
                binding.textView2.movementMethod = ScrollingMovementMethod()


                val scrollToY = stringBuilder.lines().size * binding.textView2.lineHeight - 16 * binding.textView2.lineHeight // up to 16 lines are visible and dont need scrolling
                if (scrollToY>0) {
                    binding.textView2.scrollTo(0, scrollToY)
                    //Toast.makeText(this, "lines:"+stringBuilder.lines().size+" lineheigt: "+binding.textView2.lineHeight , Toast.LENGTH_SHORT).show()
                }
        }


        // src: https://www.geeksforgeeks.org/spinner-in-kotlin/
        /*
        val languages = resources.getStringArray(R.array.eventTypes)
        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, languages
            )
            spinner.adapter = adapter
        } */

        // alternative from: https://developer.android.com/guide/topics/ui/controls/spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.eventTypes,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerEvent.adapter = adapter
        }


        ArrayAdapter.createFromResource(
            this,
            R.array.findings,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerL2.adapter = adapter
        }


        ArrayAdapter.createFromResource(
            this,
            R.array.Blutwerte,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerL3.adapter = adapter
        }






        binding.spinnerLayer1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener            {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val choice = parent?.getItemAtPosition(position).toString()
                checkLayer2(choice,parent);




            }
        }

        binding.spinnerLayer2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener            {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                spinnerL2position = position;

                val choice = parent?.getItemAtPosition(position).toString()
                checkLayer3(choice,parent);





            }
        }

        binding.spinnerLayer3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener            {

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.spinnerLayer4.visibility = View.INVISIBLE
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


                spinnerL3position = position;

                val choice = parent?.getItemAtPosition(position).toString()

                if(binding.spinnerLayer3.visibility == View.VISIBLE) {

                    checkLayer4Units(choice,parent)


                    //Toast.makeText(parent.context, " ffffffff ", Toast.LENGTH_LONG).show();
                }else
                {
                    binding.spinnerLayer4.visibility = View.INVISIBLE
                }


            }

        }



        binding.buttonRevokeLastEntry.setOnClickListener{

           // src: https://www.rosettacode.org/wiki/Remove_lines_from_a_file#Kotlin && idee das File 2 parameter hat -> https://stackoverflow.com/questions/14376807/read-write-string-from-to-a-file-in-android
            val f = File(filesDir,fileName)
            if (f.exists()) {
                var lines = f.readLines()
                val size = lines.size

                lines = lines.take(size - 1)  //+ lines.drop(size - 1)

                val text = lines.joinToString(System.lineSeparator())+"\n"
                f.writeText(text)
            }else
            {
                Toast.makeText(this, "File could not be opened", Toast.LENGTH_SHORT).show()
            }

        }




    } // end of onCreate





    fun checkLayer2(choice : String,parent: AdapterView<*>?){

        if (parent != null) {
            when (choice) {
                "Befund" ->{
                    //  Toast.makeText(parent.context, " aaaaaaa ", Toast.LENGTH_LONG).show()
                    binding.spinnerLayer2.visibility = View.VISIBLE

                    (binding.spinnerLayer2.onItemSelectedListener as AdapterView.OnItemSelectedListener).onItemSelected(binding.spinnerLayer2,null,spinnerL2position,spinnerL2position.toLong()) // z.b. Blutwert is position 2

                }
                "ff" -> Toast.makeText(parent.context, " 2 ", Toast.LENGTH_LONG).show();
                "f" -> Toast.makeText(parent.context, " 3 ", Toast.LENGTH_LONG).show();
                else -> { // Note the block
                    binding.spinnerLayer2.visibility = View.INVISIBLE
                    binding.spinnerLayer3.visibility = View.INVISIBLE
                    binding.spinnerLayer4.visibility = View.INVISIBLE
                }
            }
            // Toast.makeText(parent.context, " ffffffff ", Toast.LENGTH_LONG).show();


        };


    }



    fun checkLayer3(choice : String,parent: AdapterView<*>?){

        if (parent != null) {
            when (choice) {
                "Blutwert" ->{
                    //  Toast.makeText(parent.context, " aaaaaaa ", Toast.LENGTH_LONG).show()
                    binding.spinnerLayer3.visibility = View.VISIBLE

                    (binding.spinnerLayer3.onItemSelectedListener as AdapterView.OnItemSelectedListener).onItemSelected(binding.spinnerLayer3,null,spinnerL3position,spinnerL3position.toLong()) // ??ffne L3 Spinner mit letzter position (z.b. erythorzyten is position 0)
                }
                "ff" -> Toast.makeText(parent.context, " 2 ", Toast.LENGTH_LONG).show();
                "f" -> Toast.makeText(parent.context, " 3 ", Toast.LENGTH_LONG).show();
                else -> { // Note the block
                    binding.spinnerLayer3.visibility = View.INVISIBLE
                    binding.spinnerLayer4.visibility = View.INVISIBLE
                }
            }
            //Toast.makeText(parent.context, " ffffffff ", Toast.LENGTH_LONG).show();
        };

    }

    fun checkLayer4Units(choice : String,parent: AdapterView<*>?){

        if (parent != null ) {
            when (choice) {
                "H??moglobin" -> {
                    //  Toast.makeText(parent.context, " aaaaaaa ", Toast.LENGTH_LONG).show()

                    ArrayAdapter.createFromResource(
                        parent.context,
                        R.array.H??moglobinUnits,
                        android.R.layout.simple_spinner_item
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Specify the layout to use when the list of choices appears
                        spinnerUnits.adapter =
                            adapter // Apply the adapter to the spinner
                    }
                    binding.spinnerLayer4.visibility = View.VISIBLE
                }
                "Erythrozyten" -> {
                    // Toast.makeText(parent.context, " 2 ", Toast.LENGTH_LONG).show();

                    ArrayAdapter.createFromResource(
                        parent.context,
                        R.array.ErythrozytenUnits,
                        android.R.layout.simple_spinner_item
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Specify the layout to use when the list of choices appears
                        spinnerUnits.adapter =
                            adapter // Apply the adapter to the spinner
                    }

                    binding.spinnerLayer4.visibility = View.VISIBLE
                }
                "f" -> Toast.makeText(parent.context, " 3 ", Toast.LENGTH_LONG).show();
                else -> { // Note the block
                    binding.spinnerLayer4.visibility = View.INVISIBLE
                }
            }

        }
    }




}

