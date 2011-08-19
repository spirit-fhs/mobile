package de.fhs.spirit.activities

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.util.Log
import android.preference._
import android.os.{Bundle}
import de.fhs.spirit.tasks.VerifyLoginDataTask
import de.fhs.spirit.toilers.SpiritHelpers
import de.fhs.spirit.R

/** Handles the Settings activity
   *
   * @author Sebastian Stallenberger
   */
class Settings extends PreferenceActivity with OnSharedPreferenceChangeListener {

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    addPreferencesFromResource(R.xml.preferences);
    //TODO PreferenceActivity

    val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    Log.d("ALLPREFERENCES", pref.getAll.toString)

    val sp: SharedPreferences = getPreferenceScreen().getSharedPreferences()

    val name = findPreference("editFhsId").asInstanceOf[Preference]
    if (name.asInstanceOf[EditTextPreference].getText.equals("")) {
      name.setSummary(getString(R.string.string_notSetYet))
    } else {
      name.setSummary(sp.getString("editFhsId", ""))
    }

    val password = findPreference("editPassword").asInstanceOf[Preference]
    if (password.asInstanceOf[EditTextPreference].getText.equals("")) {
      password.setSummary(getString(R.string.string_notSetYet))
    } else {
      password.setSummary(sp.getString("editPassword", "").replaceAll(".", "*"))
    }

    val role = SpiritHelpers.getStringPrefs(Settings.this, "role", false)
    val loggedIn = findPreference("loggedIn").asInstanceOf[CheckBoxPreference]
    if (role.equals("")) {
      loggedIn.setSummary(getString(R.string.string_notLoggedIn))
    } else {

      loggedIn.setSummary(getString(R.string.string_loggedInAs) + role.toString)
    }
  }

  override def onPause() {
    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(Settings.this)
    finish()
    super.onPause()
  }

  override def onResume() {
    super.onResume()
    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(Settings.this)
  }

  override def onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
    val pref = findPreference(key).asInstanceOf[Preference]

    if (pref.isInstanceOf[EditTextPreference]) {
      val etp = pref.asInstanceOf[EditTextPreference]
      if (pref.getKey().equals("editPassword")) {
        val encryptedPass = SpiritHelpers.encryptString(etp.getText)
        etp.setText(encryptedPass) //Instant encryption
        pref.setSummary(etp.getText().replaceAll(".", "*"));
      } else {
        pref.setSummary(etp.getText());
      }

      if (pref.getKey().equals("editFhsId") || pref.getKey().equals("editPassword")) {
        val un = findPreference("editFhsId").asInstanceOf[EditTextPreference].getText
        val pwd = findPreference("editPassword").asInstanceOf[EditTextPreference].getText

        if (!un.equals("") && !pwd.equals("")) {
          val role = new VerifyLoginDataTask(Settings.this).execute(Array(un, SpiritHelpers.decryptString(pwd))).get()
          Log.d("ROLE", role.toString)
          if (role.equals("")) {
            val loggedIn = findPreference("loggedIn").asInstanceOf[CheckBoxPreference]
            loggedIn.setChecked(false)
            SpiritHelpers.setPrefs(this, "role", "", false)
            loggedIn.setSummary(getString(R.string.string_notLoggedIn))
          } else {
            val loggedIn = findPreference("loggedIn").asInstanceOf[CheckBoxPreference]
            loggedIn.setChecked(true)
            SpiritHelpers.setPrefs(this, "role", role.toString, false)
            loggedIn.setSummary(getString(R.string.string_loggedInAs) + role.toString)
          }
        } else {
          val loggedIn = findPreference("loggedIn").asInstanceOf[CheckBoxPreference]
          loggedIn.setChecked(false)
          SpiritHelpers.setPrefs(this, "role", "", false)
          loggedIn.setSummary(getString(R.string.string_enterUnAndPass))
        }
      }


    }
  }

}