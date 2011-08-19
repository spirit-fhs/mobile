package de.fhs.spirit.tests

import junit.framework.Assert._
import _root_.android.test.AndroidTestCase

class UnitTests extends AndroidTestCase {
  def testPackageIsCorrect {
    assertEquals("de.fhs.spirit", getContext.getPackageName)
  }
}