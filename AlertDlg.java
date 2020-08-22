//#preprocess

//#ifdef TRIAL
package com.mlhsoftware.MissedCallAlertDemo;
//#else
package com.mlhsoftware.MissedCallAlert;
//#endif

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.ControlledAccessException;

import net.rim.device.api.ui.MenuItem;

import java.util.*;

import net.rim.blackberry.api.phone.phonelogs.PhoneCallLogID;
import net.rim.blackberry.api.phone.phonelogs.PhoneCallLog;
import net.rim.blackberry.api.phone.phonelogs.PhoneCallLogID;
import net.rim.blackberry.api.phone.phonelogs.PhoneLogs;

import net.rim.device.api.system.EventLogger;

import net.rim.device.api.system.Application;

class AlertDlg extends PopupScreen
{
  static boolean m_bAlert;

  Refresher m_refresher;
  private long m_timerClicks;
  private long m_repeatSeconds;
  private LabelField m_timeText = null;
  private LabelField m_numberText = null;
  private LabelField m_nameText = null;
  private int m_callCount;
  private String m_phoneNumber;
  private String m_displayName = "";
  private String m_displayNumber = "";
  private String m_errorText = "";
  private boolean m_errorAccessingLog = false;


  AlertDlg( Manager manager, String phoneNumber, String errorText, long seconds )
  {
    super( manager );

    m_bAlert = true;
    m_callCount = 1;
    m_repeatSeconds = seconds;
    m_timerClicks = 15; // first alert at 15 seconds
    m_phoneNumber = phoneNumber;
    m_displayNumber = phoneNumber;
    m_errorText = errorText;
    EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Alert dlg created".getBytes(), EventLogger.DEBUG_INFO );
    System.out.println( "MCA: Alert dlg created" );

   if ( m_errorText.length() == 0 )
    {
      getCallInfo();
    }
    else
    {
      m_displayName = m_errorText;
      EventLogger.logEvent( MissedCallAlert.LOGGER_ID, m_errorText.getBytes(), EventLogger.SEVERE_ERROR );
    }

    buildScreen();
  }

  private void buildScreen()
  {
    //#ifdef TRIAL
    add( new LabelField( "Missed Call Alert Demo", LabelField.FIELD_HCENTER ) );
    //#else
    add( new LabelField( "Missed Call Alert", LabelField.FIELD_HCENTER ) );
    //#endif
    
    SeparatorField separator = new SeparatorField();
    add( separator );
    
    LabelField tag = new LabelField( "by MLHSoftware.com", LabelField.FIELD_HCENTER );
    tag.setFont( Font.getDefault().derive(Font.PLAIN, 14) );
    add( tag );

    // Trail version
    MCAOptionsProperties optionProperties = MCAOptionsProperties.fetch();
    //#ifdef TRIAL
    int daysLeft =  optionProperties.getDaysLeft();
    //#else
    int daysLeft = 999;
    //#endif

    if ( daysLeft > 0 )
    {
      //#ifdef TRIAL
      LabelField label1 = null;
      if ( daysLeft == 1 )
      {
        label1 = new LabelField( "You have " + daysLeft + " day left in your trial.", LabelField.FIELD_HCENTER );
      }
      else
      {
        label1 = new LabelField( "You have " + daysLeft + " days left in your trial.", LabelField.FIELD_HCENTER );
      }
      label1.setFont( Font.getDefault().derive(Font.BOLD, 14) );
      add( label1 );
      //#endif
      
      add( new LabelField( "", LabelField.FIELD_HCENTER ) );
      m_nameText = new LabelField( m_displayName, LabelField.FIELD_HCENTER );
      add( m_nameText );

      m_numberText = new LabelField( m_displayNumber, LabelField.FIELD_HCENTER );
      add( m_numberText );
      
      m_refresher = new Refresher();
    }
    else
    {
      add( new LabelField( "", LabelField.FIELD_HCENTER ) );
      LabelField label1 = new LabelField( "Free trial has ended.", LabelField.FIELD_HCENTER );
      label1.setFont( Font.getDefault().derive(Font.BOLD, 20) );
      add( label1 );
      LabelField label2 = new LabelField( "Buy Missed Call Alert today.", LabelField.FIELD_HCENTER );
      label2.setFont( Font.getDefault().derive(Font.BOLD, 20) );
      add( label2 );
    }

    add( new LabelField( "", LabelField.FIELD_HCENTER ) );

    HorizontalFieldManager hfm = new HorizontalFieldManager( Field.FIELD_HCENTER | Field.FIELD_BOTTOM );
    hfm.add( new OkButton() );
    add( hfm );   

    EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Alert dlg Built".getBytes(), EventLogger.DEBUG_INFO );
    System.out.println( "MCA: Alert dlg Built" );
  }  

  private boolean getCallInfo()
  {
    System.out.println( "MCA: getCallInfo" );
    boolean retVal = true;
    try
    {
      m_errorAccessingLog = false;
      PhoneCallLogID participant = new PhoneCallLogID( m_phoneNumber );
      if ( participant != null )
      {
        m_displayNumber = participant.getAddressBookFormattedNumber();
         System.out.println( "MCA: m_displayNumber 1 " + m_displayNumber );
        if ( m_phoneNumber == "" )
        {
          m_displayNumber = participant.getNumber();
          System.out.println( "MCA: m_displayNumber 2 " + m_displayNumber );
          if ( m_phoneNumber == "" )
          {
            m_displayNumber = m_phoneNumber;
            System.out.println( "MCA: m_displayNumber 3 " + m_displayNumber );
          }
        }
        m_displayName = participant.getName();
        System.out.println( "MCA: m_displayName 1 " + m_displayName );
      }
    }
    catch ( ControlledAccessException e )
    {
      m_errorAccessingLog = true;
      retVal = false;
      m_errorText = "Permission Error: Failed to access phone log info";
      EventLogger.logEvent( MissedCallAlert.LOGGER_ID, m_errorText.getBytes(), EventLogger.SEVERE_ERROR );
      System.out.println( "MCA: ControlledAccessException" + e.toString() );
    }
    catch ( Exception e )
    {
      m_errorAccessingLog = true;
      retVal = false;
      m_errorText = "Exception Error: Failed to access phone log info";
      EventLogger.logEvent( MissedCallAlert.LOGGER_ID, m_errorText.getBytes(), EventLogger.SEVERE_ERROR );
      System.out.println( "MCA: Exception" + e.toString() );
    }

    return retVal;
  }

  public void updateUI()
  {
    if ( m_callCount == 1 )
    {
      if ( m_nameText != null )
      {
        m_nameText.setText( m_displayName );
        System.out.println( "MCA: updateUI m_displayName" + m_displayName );
      }
      m_numberText.setText( m_displayNumber );
      System.out.println( "MCA: updateUI m_displayNumber" + m_displayNumber );
    }
  }

  public void incCallCount()
  {
    m_callCount++;
    if ( m_nameText != null )
    {
      m_nameText.setText( "" );
    }
    m_numberText.setText( m_callCount + " missed calls" );
  }



  public void CloseDlg()
  {
    m_bAlert = false;
    m_refresher = null;
    clearAlert();
    close();
    MissedCallAlert.dlgDismissed();
  }

  public void update()
  {
    m_timerClicks--;

    if ( m_timerClicks < 0  )
    {
      clearAlert();
      soundAlert();
      m_timerClicks = m_repeatSeconds;
    }

    if ( m_errorAccessingLog )
    {
      if ( getCallInfo() )
      {
        Application app = Application.getApplication();
        if ( app != null )
        {
          app.invokeLater( new Runnable() { public void run() { updateUI(); } } );
        }
      }
    }
  }

  public void soundAlert()
  {
    NotificationsManager.triggerImmediateEvent( MissedCallAlert.NOTIFICATIONS_ID_1, 0, this, null );
    EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Sound Alert".getBytes(), EventLogger.DEBUG_INFO );
  }

  public void clearAlert()
  {
    NotificationsManager.cancelImmediateEvent( MissedCallAlert.NOTIFICATIONS_ID_1, 0, this, null );
    EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Clear Alert".getBytes(), EventLogger.DEBUG_INFO );
  }

  public boolean keyChar(char key, int status, int time)
  {
    switch (key)
    {
      case Characters.ESCAPE:
        CloseDlg();
        break;

      default:
        break;
    }

    return super.keyChar( key, status, time);
  }

  private final class OkButton extends ButtonField
  {
    private OkButton()
    {
      super( "OK", ButtonField.CONSUME_CLICK );
    }

    protected void fieldChangeNotify( int context )
    {
      if ( ( context & FieldChangeListener.PROGRAMMATIC ) == 0 )
      {
        CloseDlg();
      }
    }
  }

  private class Refresher extends Thread
  {
    // When the object is created it starts itself as a thread
    Refresher()
    {
      EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Alert Thread created".getBytes(), EventLogger.DEBUG_INFO );
      start();
    }

    // This method defines what this thread does every time it runs.
    public void run()
    {
      try
      {
        while ( m_bAlert )
        {
          update();
          try
          {
            this.sleep( 1000 );
          }
          catch ( InterruptedException e )
          {
            String msg = "Thread sleep: " + e.toString();
            EventLogger.logEvent( MissedCallAlert.LOGGER_ID, msg.getBytes(), EventLogger.SEVERE_ERROR );
            // Do nothing if we couldn't sleep, we don't care about exactly perfect timing.
          }
        }
      }
      catch ( Exception e )
      {
        String msg = "Thread: " + e.toString();
        EventLogger.logEvent( MissedCallAlert.LOGGER_ID, msg.getBytes(), EventLogger.SEVERE_ERROR );
      }
    }
  }  
}
