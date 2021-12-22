package com.fdsanchez.ticonline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class GameActivity : AppCompatActivity() {
    val turnoDe = "Turno de "
    var currentPlayer = 0
    var player1Sign = ""
    var player2Sign = ""

    var turnTV : TextView? = null
    var restartBtn : Button? = null
    var inputLayout : LinearLayout? = null

    var etEmail : EditText? = null
    var buttonsList  = arrayOf<Button>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        myEmail = intent.getStringExtra("email")

        findViewById<Button>(R.id.idBtnRequest).setOnClickListener {
            buRequestEvent(findViewById<Button>(R.id.idBtnRequest))
        }

        findViewById<Button>(R.id.idBtnAccept).setOnClickListener {
            buAcceptEvent(findViewById<Button>(R.id.idBtnAccept))
        }


        turnTV = findViewById(R.id.turn)
       // var mode = intent.getStringExtra("mode")
        var mode = "online"
        etEmail = findViewById(R.id.etEmail)


        inputLayout = findViewById(R.id.idOnlineInput)
        inputLayout?.visibility = View.GONE

        restartBtn = findViewById(R.id.idRestartBtn)
        restartBtn?.setOnClickListener{
            cleanBtnsArray()
            restartBtn?.visibility = View.GONE
            myRef.child("PlayerOnline").child(sessionID!!).child("moves").removeValue()

        }
        restartBtn?.visibility = View.GONE


        // get buttons
        buttonsList = arrayOf(
            findViewById(R.id.idBtn0),
            findViewById(R.id.idBtn1),
            findViewById(R.id.idBtn2),
            findViewById(R.id.idBtn3),
            findViewById(R.id.idBtn4),
            findViewById(R.id.idBtn5),
            findViewById(R.id.idBtn6),
            findViewById(R.id.idBtn7),
            findViewById(R.id.idBtn8),

        )


        if(mode.equals("online")) {
            online()
            inputLayout?.visibility = View.VISIBLE
            fillButtonsEventOnline()
            IncommingCalls()
        }
        else{
            local()
            fillButtonsEventLocal()
            inputLayout?.visibility = View.GONE
        }

        cleanBtnsArray()


    }



    fun local(){
        turnTV?.setText(turnoDe + "X")
        currentPlayer = 1;
    }
    fun online(){
// sesion instacion jugador

    }


    fun cleanBtnsArray(){
        for (b in buttonsList){
            b.setText("")
            b.isEnabled = true


        }
     //   myRef.child("PlayerOnline").removeValue()
    //    turnTV?.setText(turnoDe + GetCurrentUserSign())
    }

    fun DisableAllButtons(){
        for (b in buttonsList){

            b.isEnabled = false
        }
    }

    fun fillButtonsEventLocal(){
        loop@ for (b in buttonsList){
            b.setOnClickListener{
                b.setText(GetCurrentUserSign());
                b.isEnabled = false
                Toast.makeText(this,"Presiono el botton", Toast.LENGTH_LONG).show()


                if(checkVictory()){
                    turnTV?.setText("Ganador es " +  GetCurrentUserSign())
                    DisableAllButtons()
                    println("gano " + GetCurrentUserSign())
                    restartBtn?.visibility = View.VISIBLE

                    return@setOnClickListener
                }else{
                    ChangeTurn()
                    turnTV?.setText(turnoDe + GetCurrentUserSign())
                }

                if(AreAllPressed()){
                    turnTV?.setText("Tablas")
                    restartBtn?.visibility = View.VISIBLE
                    return@setOnClickListener
                }


            }
        }
    }
    fun GetCurrentUserSign(): String{
        if (currentPlayer == 1){

            return "X"
        }else if (currentPlayer == 2){

            return "0"
        }
        return "e"
    }
    fun ChangeTurn(){
        if (currentPlayer == 1){
            currentPlayer = 2
        }else if (currentPlayer == 2){
            currentPlayer = 1
        }
    }

    fun checkButtons(id1 : Int,id2 : Int,id3 : Int) : Boolean{
     //   println("gana prueba ")
        if(buttonsList[id1].text.toString().equals(buttonsList[id2].text.toString()) &&
            buttonsList[id1].text.toString().equals(buttonsList[id3].text.toString())
            && buttonsList[id1].text.toString() != ""
        ){
            println("ganador $id1$id2$id3")
            return true
        }
        return false
    }

    fun checkVictory() : Boolean{
        // horizontal
       if(checkButtons(0,1,2)) return true
        else if(checkButtons(3,4,5)) return true
        else if (checkButtons(6,7,8)) return true

        //vertical
       else if(checkButtons(0,3,6)) return true
       else if (checkButtons(1,4,7)) return true
       else if (checkButtons(2,5,8)) return true
        //diagonal
       else if(checkButtons(0,4,8)) return true
       else if (checkButtons(2,4,6)) return true

        return false
    }

    fun AreAllPressed(): Boolean{

        for (b in buttonsList){
            if(b.isEnabled){

                    return false

            }
        }
        return true
    }

    // Online -----------------------------------------------

    //database instance
    private var database= FirebaseDatabase.getInstance()
    private var myRef=database.reference

    var myEmail:String?=null

    //   private var mFirebaseAnalytics: FirebaseAnalytics?=null
    var player1= java.util.ArrayList<Int>()
    var player2= java.util.ArrayList<Int>()
    var ActivePlayer=1
    var PlayerSymbol = ""

    protected fun buRequestEvent(view:android.view.View){
         userDemail=findViewById<EditText>(R.id.etEmail).text.toString()
        cleanBtnsArray()

        myRef.child("Users").child(SplitString(userDemail)).child("Request").push().setValue(myEmail)


        PlayerOnline(SplitString(myEmail!!)+ SplitString(userDemail)) // husseinjena
        PlayerSymbol="X"
        val myToast = Toast.makeText(this, "usuario:$myEmail request a $userDemail", Toast.LENGTH_LONG)
        myToast.show()
        ;
    }
    var userDemail = ""
    protected fun buAcceptEvent(view:android.view.View){
         userDemail=findViewById<EditText>(R.id.etEmail).text.toString()
        myRef.child("Users").child( SplitString(userDemail)).child("Request").push().setValue(myEmail)

        val myToast = Toast.makeText(this, "session: " + SplitString(userDemail)+SplitString(myEmail!!), Toast.LENGTH_LONG)
        myToast.show()
        PlayerOnline(SplitString(userDemail)+SplitString(myEmail!!)) //husseinjena
        myRef.child("PlayerOnline").child(SplitString(userDemail)+SplitString(myEmail!!)).child("signs").child(SplitString(myEmail!!)).setValue("X")
        myRef.child("PlayerOnline").child(SplitString(userDemail)+SplitString(myEmail!!)).child("signs").child(SplitString(userDemail)).setValue("0")
        myRef.child("PlayerOnline").child(SplitString(userDemail)+SplitString(myEmail!!)).child("signs").child("currentTurn").setValue(SplitString(myEmail!!))
        cleanBtnsArray();
        PlayerSymbol="O"

        //https://github.com/hussien89aa/KotlinUdemy/blob/master/Android/TicTacToy%20Game/TicTacToyOnline/TicTacToyLocal/StartUp/app/src/main/java/com/hussein/startup/MainActivity.kt

    }
    var userTurn = ""
    fun fillButtonsEventOnline(){
        var cellID = 0
        loop@ for (b in buttonsList){
            b.setOnClickListener{
               // b.setText(GetCurrentUserSign());
                //b.isEnabled = false

                if(sessionID != ""){

                    var cellID=0
                    when(b.id){
                        R.id.idBtn0-> cellID=0
                        R.id.idBtn1-> cellID=1
                        R.id.idBtn2-> cellID=2
                        R.id.idBtn3-> cellID=3
                        R.id.idBtn4-> cellID=4
                        R.id.idBtn5-> cellID=5
                        R.id.idBtn6-> cellID=6
                        R.id.idBtn7-> cellID=7
                        R.id.idBtn8-> cellID=8

                    }
                  //  Toast.makeText(this,"ID:"+ cellID, Toast.LENGTH_LONG).show()
                    if(userTurn != "" && userTurn.equals(SplitString(myEmail!!))){
                        println(" session: $sessionID id: $cellID, tu email: $myEmail ")
                        myRef.child("PlayerOnline").child(sessionID!!).child("moves").child(cellID.toString()).setValue(myEmail)
                        println("User demail$userDemail")
                        myRef.child("PlayerOnline").child(sessionID!!).child("signs").child("currentTurn").setValue(SplitString(userDemail))


                    }else{
                        Toast.makeText(this,"No es tu turno", Toast.LENGTH_LONG).show()

                    }
                   cellID++



                }else{
                    Toast.makeText(this,"No existe session, debe ingresar el correro del otro jugador", Toast.LENGTH_LONG).show()
                }



            }
        }
    }

    var sessionID:String?=""

    fun PlayerOnline(sessionID:String){
        this.sessionID=sessionID


        myRef.child("PlayerOnline").child(sessionID).child("signs").child("currentTurn")
            .addValueEventListener(object :  ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val td=snapshot!!.value as String
                        if(td.equals(SplitString(myEmail!!))){
                            turnTV?.setText("Tu turno")
                        }else{
                            turnTV?.setText(turnoDe + td)
                        }
                        userTurn = td

                        if(checkVictory()){
                            // have to do this beacuse it change the turn and then show the winner, so have to show the player that is not the current user
                            if(userTurn == SplitString(myEmail!!)){
                                turnTV?.setText("Ganador es " +  userDemail)
                            }else{
                                turnTV?.setText("Ganador es " +  myEmail)
                            }

                            DisableAllButtons()
                            restartBtn?.visibility = View.VISIBLE
                        }else
                        if(AreAllPressed()){
                            turnTV?.setText("Tablas")
                            restartBtn?.visibility = View.VISIBLE
                        }

                    }catch (e :Exception){}
                }
                override fun onCancelled(error: DatabaseError) {}

            })

        // get signs
        myRef.child("PlayerOnline").child(sessionID).child("signs")
            .addValueEventListener(object :  ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val td=snapshot!!.value as HashMap<String,String>

                        player1Sign  = td[SplitString(myEmail!!)] as String
                     //   println ("player1Sign $player1Sign")
                        player2Sign  = td[SplitString(userDemail)] as String

                      //  println ("player2Sign $player2Sign")


                    }catch (e :Exception){
                        e.printStackTrace()
                    }
                }
                override fun onCancelled(error: DatabaseError) {}

            })

        myRef.child("PlayerOnline").child(sessionID).child("moves").removeValue()
        myRef.child("PlayerOnline").child(sessionID).child("moves")
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    try{
                        println("Nuevo movimiento -------")
                        for (m in snapshot.children){
                            println("Nuevo movimiento key:" + m.key + ", value: " + m.value)
                            if(m.value == myEmail!!){
                                OnlineButtonPressed(buttonsList[m.key!!.toInt()],player1Sign)
                            }else{
                                OnlineButtonPressed(buttonsList[m.key!!.toInt()],player2Sign)
                            }
                        }

                        if(!snapshot.exists()){
                            cleanBtnsArray()
                        }

                    }catch (ex:Exception){}
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

    fun OnlineButtonPressed(b : Button, sign : String){
        if(b.isEnabled){
            println("changed button to " + sign )
            b.setText(sign)
            b.isEnabled = false


        }


    }

    var context  = this
    fun IncommingCalls(){
        myRef.child("Users").child(SplitString(myEmail!!)).child("Request")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    try{
                        val td=snapshot!!.value as HashMap<String,Any>
                        if(td!=null){
                            Toast.makeText(context,"ID:"+ "session request added", Toast.LENGTH_LONG).show()
                            var value:String

                            for (key in td.keys){
                                value= td[key] as String
                                etEmail?.setText(value)


                                myRef.child("Users").child(SplitString(myEmail!!)).child("Request").setValue(true)

                              //  val notifyme=Notifications()
                             //   notifyme.Notify(applicationContext,value + " want to play tic tac toy",number)
                              //  number++


                                break

                            }

                        }

                    }catch (ex:Exception){}

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


    }



    fun  SplitString(str:String):String{
        var split=str.split("@")
        return split[0]
    }


}