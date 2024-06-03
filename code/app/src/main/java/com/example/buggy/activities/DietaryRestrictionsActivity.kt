package com.example.buggy.activities

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import androidx.activity.ComponentActivity
import com.example.buggy.R
import com.example.buggy.data.appdata.Allergies
import com.example.buggy.data.appdata.DietaryRestrictions
import com.example.buggy.network.FirestoreManager


@SuppressLint("UseSwitchCompatOrMaterialCode")
class DietaryRestrictionsActivity: ComponentActivity() {

    private lateinit var back_button: Button
    // Dietary restriction buttons
    private lateinit var halal_switch: Switch
    private lateinit var kosher_switch: Switch
    private lateinit var vegetarian_switch: Switch
    private lateinit var vegan_switch: Switch
    private lateinit var keto_switch: Switch
    private lateinit var low_carb_switch: Switch
    private lateinit var pescetarian_switch: Switch
    private lateinit var paleo_switch: Switch
    private lateinit var low_sodium_switch: Switch
    // Allergy buttons
    private lateinit var dairy_switch: Switch
    private lateinit var egg_switch: Switch
    private lateinit var gluten_switch: Switch
    private lateinit var grain_switch: Switch
    private lateinit var peanut_switch: Switch
    private lateinit var seafood_switch: Switch
    private lateinit var sesame_switch: Switch
    private lateinit var shellfish_switch: Switch
    private lateinit var soy_switch: Switch
    private lateinit var treenut_switch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dietary_restrictions)

        // Dietary Restrictions
        halal_switch = findViewById(R.id.halalSwitch)
        kosher_switch = findViewById(R.id.kosherSwitch)
        vegetarian_switch = findViewById(R.id.vegetarianSwitch)
        vegan_switch = findViewById(R.id.veganSwitch)
        keto_switch = findViewById(R.id.ketoSwitch)
        low_carb_switch = findViewById(R.id.lowCarbSwitch)
        pescetarian_switch = findViewById(R.id.pescetarianSwitch)
        paleo_switch = findViewById(R.id.paleoSwitch)
        low_sodium_switch = findViewById(R.id.lowSodiumSwitch)

        val restrictions = BooleanArray(9)
        FirestoreManager.getRestrictions { restrictions ->
            setRestrictions(restrictions)
            Log.d(TAG, "Retrieved restrictions")
        }

        val restriction_switches = arrayOf(halal_switch, kosher_switch, vegetarian_switch, vegan_switch,
            keto_switch, low_carb_switch, pescetarian_switch, paleo_switch, low_sodium_switch)

        for (i in restriction_switches.indices) {
            restriction_switches[i].setOnCheckedChangeListener { buttonView, isChecked ->
                restrictions[i] = buttonView.isChecked
            }
        }
        // Allergies
        dairy_switch = findViewById(R.id.dairySwitch)
        egg_switch = findViewById(R.id.eggSwitch)
        gluten_switch = findViewById(R.id.glutenSwitch)
        grain_switch = findViewById(R.id.grainSwitch)
        peanut_switch = findViewById(R.id.peanutSwitch)
        seafood_switch = findViewById(R.id.seafoodSwitch)
        sesame_switch = findViewById(R.id.sesameSwitch)
        shellfish_switch = findViewById(R.id.shellfishSwitch)
        soy_switch = findViewById(R.id.soySwitch)
        treenut_switch = findViewById(R.id.treenutSwitch)

        val allergies = BooleanArray(10)
        FirestoreManager.getAllergies { allergies ->
            setAllergies(allergies)
            Log.d(TAG, "Retrieved allergies")
        }

        val allergy_switches = arrayOf(dairy_switch, egg_switch, gluten_switch, grain_switch,
            peanut_switch, seafood_switch, sesame_switch, shellfish_switch, soy_switch, treenut_switch)

        for (i in allergy_switches.indices) {
            allergy_switches[i].setOnCheckedChangeListener { buttonView, isChecked ->
                allergies[i] = buttonView.isChecked
            }
        }

        back_button = findViewById(R.id.dietaryRestrictionsBackButton)

        back_button.setOnClickListener {
            FirestoreManager.changeRestrictions(restrictions)
            FirestoreManager.changeAllergies(allergies)
            setRestrictions(restrictions)
            setAllergies(allergies)
            startActivity(Intent(this,RecipesActivity::class.java))
            finish()
        }
    }

    fun setRestrictions(restrictions: BooleanArray) {
        halal_switch.isChecked = restrictions[0]
        kosher_switch.isChecked = restrictions[1]
        vegetarian_switch.isChecked = restrictions[2]
        vegan_switch.isChecked = restrictions[3]
        keto_switch.isChecked = restrictions[4]
        low_carb_switch.isChecked = restrictions[5]
        pescetarian_switch.isChecked = restrictions[6]
        paleo_switch.isChecked = restrictions[7]
        low_sodium_switch.isChecked = restrictions[8]

        DietaryRestrictions.setRestriction(restrictions)
    }

    fun setAllergies(allergies: BooleanArray) {
        dairy_switch.isChecked = allergies[0]
        egg_switch.isChecked = allergies[1]
        gluten_switch.isChecked = allergies[2]
        grain_switch.isChecked = allergies[3]
        peanut_switch.isChecked = allergies[4]
        seafood_switch.isChecked = allergies[5]
        sesame_switch.isChecked = allergies[6]
        shellfish_switch.isChecked = allergies[7]
        soy_switch.isChecked = allergies[8]
        treenut_switch.isChecked = allergies[9]

        Allergies.setAllergy(allergies)
    }
}