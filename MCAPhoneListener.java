//#preprocess

//#ifdef TRIAL
package com.mlhsoftware.MissedCallAlertDemo;
//#else
package com.mlhsoftware.MissedCallAlert;
//#endif

import net.rim.blackberry.api.phone.AbstractPhoneListener;
import net.rim.device.api.ui.UiApplication;

import net.rim.blackberry.api.phone.PhoneCall;
import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.system.ControlledAccessException;

import net.rim.device.api.system.EventLogger;

public class MCAPhoneListener extends AbstractPhoneListener 
{
  private boolean m_bCallAnswered;
  private boolean m_bIncomingCall;
  public String m_phoneNumber;

  public MCAPhoneListener()
  {
    m_bCallAnswered = false;
    m_bIncomingCall = false;
    EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "MCAPhoneListener created".getBytes(), EventLogger.ALWAYS_LOG );
  }

  public void callIncoming( int callId )
  {
    m_bIncomingCall = true;
    m_phoneNumber = "";
    PhoneCall phoneCall = Phone.getCall( callId );
    if ( phoneCall != null )
    {
      m_phoneNumber = phoneCall.getDisplayPhoneNumber();
    }
    EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Incoming call".getBytes(), EventLogger.DEBUG_INFO );
    //System.out.println( "MLH - callIncoming" );
  }

  public void callAnswered( int callId )
  {
    m_bCallAnswered = true;
    EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Call Answered".getBytes(), EventLogger.DEBUG_INFO );
    //System.out.println( "MLH - callAnswered" );
  }

  public void callDisconnected( int callId )
  {
    EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Call Disconnected".getBytes(), EventLogger.DEBUG_INFO );
    if ( m_bIncomingCall && !m_bCallAnswered )
    {
      MissedCallAlert.missedCall( m_phoneNumber, "" );
      //MissedCallAlert.missedCall( "" );
    }

    m_bCallAnswered = false;
    m_bIncomingCall = false;
    //System.out.println( "MLH - callDisconnected" );
  }


  public void callInitiated( int callid )
  {
    //System.out.println( "MLH - callInitiated" );
  }

  public void callConnected( int callId )
  {
    EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Call Connected".getBytes(), EventLogger.DEBUG_INFO );
    m_bCallAnswered = true;
    //System.out.println( "MLH - callConnected" );
  }

  public void callWaiting( int callid )
  {
    //System.out.println( "MLH - callWaiting" );
  }

  public void callConferenceCallEstablished( int callId )
  {
    //System.out.println( "MLH - callConferenceCallEstablished" );
  }

  public void conferenceCallDisconnected( int callId )
  {
    //System.out.println( "MLH - conferenceCallDisconnected" );
  }

  public void callDirectConnectConnected( int callId )
  {
    //System.out.println( "MLH - callDirectConnectConnected" );
  }

  public void callDirectConnectDisconnected( int callId )
  {
    //System.out.println( "MLH - callDirectConnectDisconnected" );
  }

  public void callEndedByUser( int callId )
  {
    //System.out.println( "MLH - callEndedByUser" );
  }

  public void callFailed( int callId, int reason )
  {
    //System.out.println( "MLH - callFailed" );
  }

  public void callResumed( int callId )
  {
    //System.out.println( "MLH - callResumed" );
  }

  public void callHeld( int callId )
  {
    //System.out.println( "MLH - callHeld" );
  }

  public void callAdded( int callId )
  {
    //System.out.println( "MLH - callAdded" );
  }

  public void callRemoved( int callId )
  {
    //System.out.println( "MLH - callRemoved" );
  }
}
