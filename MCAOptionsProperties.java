//#preprocess

//#ifdef TRIAL
package com.mlhsoftware.MissedCallAlertDemo;
//#else
package com.mlhsoftware.MissedCallAlert;
//#endif

import net.rim.device.api.system.*;
import net.rim.device.api.util.*;
import java.util.*;

//The configuration properties of the OptionsSample application. One instance holding
//the effective values resides in the persistent store.
class MCAOptionsProperties implements Persistable
{
  private boolean m_bEnableAlert;
  private int m_frequency;
  private long m_refDate;

  private static final long DAYS_IN_MS = 1000 * 60 * 60 * 24;  //86400000; // 24(h) * 60(m) * 60(s) * 1000(ms); 
  private static final long TRIAL_LENGTH = 7;

  //#ifdef TRIAL
  private static final long PERSISTENCE_ID = 0xd1afd38391484c6eL;   //Hash of com.mlhsoftware.MissedCallAlertDemo.options.MCAOptionsProperties
  //#else
  private static final long PERSISTENCE_ID = 0x9218a55a279a23e4L;   //Hash of com.mlhsoftware.MissedCallAlert.options.MCAOptionsProperties
  //#endif


  //Persistent object wrapping the effective properties instance
  private static PersistentObject store;
  private static boolean firstRun = false;

    //Ensure that an effective properties set exists on startup.
  static
  {
    store = PersistentStore.getPersistentObject( PERSISTENCE_ID );
    synchronized ( store )
    {
      if ( store.getContents() == null )
      {
        firstRun = true;
        store.setContents( new MCAOptionsProperties() );
        store.commit();
      }
    }
  }

  // Constructs a properties set with default values.
  private MCAOptionsProperties()
  {
    m_bEnableAlert = true;
    m_frequency = 1;
    long now = new Date().getTime();
    m_refDate = now + ( TRIAL_LENGTH * DAYS_IN_MS );
  }

  public static boolean isFirstRun()
  {
    return firstRun;
  }

  //Retrieves a copy of the effective properties set from storage.
  public static MCAOptionsProperties fetch()
  {
    MCAOptionsProperties savedProps = (MCAOptionsProperties)store.getContents();
    return new MCAOptionsProperties( savedProps );
  }

  //Causes the values within this instance to become the effective
  //properties for the application by saving this instance to the store.
  public void save()
  {
    store.setContents( this );
    store.commit();
  }

  public boolean getEnableAlert()
  {
    return m_bEnableAlert;
  }

  public void setEnableAlert( boolean bEnableAlert )
  {
    m_bEnableAlert = bEnableAlert;
  }

  public int getFrequency()
  {
    return m_frequency;
  }

  public void setFrequency( int frequency )
  {
    m_frequency = frequency;
  }

  public int getSeconds()
  {
    int seconds = 60;    // default is 1 minute
    switch ( m_frequency )
    {
      case 0:
      { // 30 Sec.
        seconds = 30;
        break;
      }
      case 1:
      { // 1Min.
        seconds = 60;
        break;
      }
      case 2:
      { // 2 Min.
        seconds = 120;
        break;
      }
      case 3:
      { // 5 Min.
        seconds = 300;
        break;
      }
      case 4:
      { // 10 Min.
        seconds = 600;
        break;
      }
      case 5:
      { // 15 Min.
        seconds = 900;
        break;
      }
  }
    return seconds;
  }

  public int getDaysLeft()
  {
    long now = new Date().getTime();
    long timeLeft = m_refDate - now;

    int daysLeft = (int)(timeLeft / DAYS_IN_MS);
    return daysLeft + 1;
  }


  //Cannonical copy constructor.
  private MCAOptionsProperties( MCAOptionsProperties other )
  {
    m_bEnableAlert = other.m_bEnableAlert;
    m_frequency = other.m_frequency;
    m_refDate = other.m_refDate;
  }
}
