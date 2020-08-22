//#preprocess

package com.mlhsoftware;


import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.system.Bitmap;

import net.rim.device.api.ui.container.MainScreen;
import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MessageArguments;
import net.rim.device.api.system.CodeModuleManager;

import net.rim.device.api.system.DeviceInfo;

import net.rim.device.api.system.EventLogger;

public final class AboutDialog extends MainScreen 
{
  String APP_VERSION = "1.0.0.6";
  String APP_NAME = "Missed Call Alert";
  String APP_URL = "http://m.mlhsoftware.com";
  String TELL_A_FRIEND = "Missed Call Alert detects missed phone calls and creates a recurring audible alert based on active profile settings. Check it out at http://www.mlhsoftware.com";

  String m_deviceName;
  String m_deviceOS;

  public AboutDialog() 
  {
    super( DEFAULT_MENU | DEFAULT_CLOSE );
    LabelField title = new LabelField( "About " + APP_NAME, LabelField.FIELD_HCENTER  );
    title.setFont( Font.getDefault().derive( Font.BOLD, 20 ) );
    setTitle( title  );

    m_deviceName = DeviceInfo.getDeviceName();
    m_deviceOS = CodeModuleManager.getModuleVersion( CodeModuleManager.getModuleHandleForObject( "" ) );

    add( new NullField( Field.FOCUSABLE ) );
    add( new LabelField( "" ) );
    add( new LabelField( APP_NAME, LabelField.FIELD_HCENTER ) );
    add( new LabelField( APP_VERSION, LabelField.FIELD_HCENTER ) );
    //add( new SeparatorField() );

    add( new LabelField( "" ) );
    add( new LabelField( "Blackberry", LabelField.FIELD_HCENTER ) );
    add( new LabelField( m_deviceName, LabelField.FIELD_HCENTER ) );
    add( new LabelField( m_deviceOS, LabelField.FIELD_HCENTER ) );
    //add( new SeparatorField() );
    add( new LabelField( "" ) );

    add( new LabelField( "MLH Software", LabelField.FIELD_HCENTER ) );
    add( new LabelField( "www.mlhsoftware.com", LabelField.FIELD_HCENTER ) );
    add( new NullField( Field.FOCUSABLE ) );
    //add( new LabelField( "" ) );

 //   add( new TellAFriendButton() );
 //   add( new FeedbackButton() );
 //   add( new MoreInfoButton() );
 //   add( new OkButton() );


 //   addMenuItem( _close );
    addMenuItem( _moreInfo );
    addMenuItem( _tellFriend );
    addMenuItem( _feedBack );
    addMenuItem( MenuItem.separator( 3000 ) );
    addMenuItem( _debug );
    addMenuItem( _showLog );
  }


  private final class TellAFriendButton extends ButtonField
  {
    private TellAFriendButton()
    {
      super( "Tell a friend", ButtonField.CONSUME_CLICK | LabelField.USE_ALL_WIDTH | LabelField.FIELD_HCENTER );
    }

    protected void fieldChangeNotify( int context )
    {
      if ( ( context & FieldChangeListener.PROGRAMMATIC ) == 0 )
      {
        String arg = MessageArguments.ARG_NEW;
        String to = "";
        String subject = "Check out this Blackberry application: " + APP_NAME;
        String body = TELL_A_FRIEND;

        MessageArguments msgArg = new MessageArguments( arg, to, subject, body );
        Invoke.invokeApplication( Invoke.APP_TYPE_MESSAGES, msgArg );
      }
    }
  }

  private final class FeedbackButton extends ButtonField
  {
    private FeedbackButton()
    {
      super( "Feedback", ButtonField.CONSUME_CLICK | LabelField.USE_ALL_WIDTH | LabelField.FIELD_HCENTER );
    }

    protected void fieldChangeNotify( int context )
    {
      if ( ( context & FieldChangeListener.PROGRAMMATIC ) == 0 )
      {
        String arg = MessageArguments.ARG_NEW;
        String to = "feedback@mlhsoftware.com";
        String subject = "Feedback: " + APP_NAME;
        String body = "Tell us what you think about " + APP_NAME + ". \r\n\r\n Version: " + APP_VERSION + " \r\n Device: " + m_deviceName + " \r\n OS Version: " + m_deviceOS + " \r\n\r\n";

        MessageArguments msgArg = new MessageArguments( arg, to, subject, body );
        Invoke.invokeApplication( Invoke.APP_TYPE_MESSAGES, msgArg );
      }
    }
  }

  private final class MoreInfoButton extends ButtonField
  {
    private MoreInfoButton()
    {
      super( "More Info", ButtonField.CONSUME_CLICK | LabelField.USE_ALL_WIDTH | LabelField.FIELD_HCENTER );
    }

    protected void fieldChangeNotify( int context )
    {
      if ( ( context & FieldChangeListener.PROGRAMMATIC ) == 0 )
      {
        Browser.getDefaultSession().displayPage( APP_URL );
      }
    }
  }

  private final class OkButton extends ButtonField
  {
    private OkButton()
    {
      super( "OK", ButtonField.CONSUME_CLICK | LabelField.USE_ALL_WIDTH | LabelField.FIELD_HCENTER );
    }

    protected void fieldChangeNotify( int context )
    {
      if ( ( context & FieldChangeListener.PROGRAMMATIC ) == 0 )
      {
        close();
      }
    }
  }


  private MenuItem _close = new MenuItem( "Close", 1, 1 )
  {
    public void run()
    {
      close();
    }
  };

  
  private MenuItem _tellFriend = new MenuItem( "Tell a friend", 1, 1000 )
  {
    public void run()
    {
      String arg = MessageArguments.ARG_NEW;
      String to = "";
      String subject = "Check out this Blackberry application: " + APP_NAME;
      String body = TELL_A_FRIEND;

      MessageArguments msgArg = new MessageArguments( arg, to, subject, body );
      Invoke.invokeApplication( Invoke.APP_TYPE_MESSAGES, msgArg );
    }
  };

  private MenuItem _feedBack = new MenuItem( "Feedback", 2, 1001 )
  {
    public void run()
    {
      String arg = MessageArguments.ARG_NEW;
      String to = "feedback@mlhsoftware.com";
      String subject = "Feedback: " + APP_NAME;
      String body = "Tell us what you think about " + APP_NAME + ". \r\n\r\n Version: " + APP_VERSION + " \r\n Device: " + m_deviceName + " \r\n OS Version: " + m_deviceOS + " \r\n\r\n";

      MessageArguments msgArg = new MessageArguments( arg, to, subject, body );
      Invoke.invokeApplication( Invoke.APP_TYPE_MESSAGES, msgArg );
    }
  };

  private MenuItem _moreInfo = new MenuItem( "More Info", 3, 1002 )
  {
    public void run()
    {
      Browser.getDefaultSession().displayPage( APP_URL );
    }
  };


  private MenuItem _debug = new MenuItem( "Enable Debug Logging", 3001, 1003 )
  {
    public void run()
    {
      EventLogger.setMinimumLevel( EventLogger.DEBUG_INFO );
      Dialog.alert( "Debug Logging has been enabled" );
    }
  };

  private MenuItem _showLog = new MenuItem( "Open Event Logs", 3002, 1004 )
  {
    public void run()
    {
      EventLogger.startEventLogViewer();
    }
  };

}
