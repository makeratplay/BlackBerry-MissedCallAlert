//#preprocess

//#ifdef TRIAL
package com.mlhsoftware.MissedCallAlertDemo;
//#else
package com.mlhsoftware.MissedCallAlert;
//#endif

import net.rim.blackberry.api.options.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;

import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationManagerException;

import net.rim.device.api.ui.component.Dialog;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MessageArguments;
import net.rim.device.api.system.CodeModuleManager;

import net.rim.device.api.system.DeviceInfo;

class MCAOptionsProvider implements OptionsProvider
{
  private ObjectChoiceField enableChoice;
  private ObjectChoiceField frequencyUnitsChoice;

  private MCAOptionsProperties optionProperties;

  String APP_VERSION = "1.0.0.4";
  String APP_NAME = "Missed Call Alert";
  String APP_URL = "http://m.mlhsoftware.com";
  String TELL_A_FRIEND = "Missed Call Alert detects missed phone calls and creates a recurring audible alert based on active profile settings. Check it out at http://www.mlhsoftware.com";

  String m_deviceName;
  String m_deviceOS;


  public MCAOptionsProvider()
  {
    m_deviceName = DeviceInfo.getDeviceName();
    m_deviceOS = CodeModuleManager.getModuleVersion( CodeModuleManager.getModuleHandleForObject( "" ) );
  }

  public String getTitle()
  {
    //#ifdef TRIAL
    return "Missed Call Alert Demo";
    //#else
    return "Missed Call Alert";
    //#endif
  }

  public void populateMainScreen( MainScreen mainScreen )
  {
    //Read in the properties from the persistent store.
    optionProperties = MCAOptionsProperties.fetch();

    LabelField tag = new LabelField( "by MLHSoftware.com", LabelField.FIELD_HCENTER );
    tag.setFont( Font.getDefault().derive( Font.PLAIN, 12 ) );
    mainScreen.add( tag );
    mainScreen.add( new LabelField( "", LabelField.FIELD_HCENTER ) );

    // Trail version
    //#ifdef TRIAL
    int daysLeft = optionProperties.getDaysLeft();
    if ( daysLeft > 1 )
    {
      LabelField label1 = new LabelField( "You have " + daysLeft + " days left in your trial.", LabelField.FIELD_HCENTER );
      label1.setFont( Font.getDefault().derive( Font.BOLD, 14 ) );
      mainScreen.add( label1 );
    }
    else if ( daysLeft == 1 )
    {
      LabelField label1 = new LabelField( "You have " + daysLeft + " day left in your trial.", LabelField.FIELD_HCENTER );
      label1.setFont( Font.getDefault().derive( Font.BOLD, 14 ) );
      mainScreen.add( label1 );
    }
    else
    {
      LabelField label1 = new LabelField( "Free trial has ended.", LabelField.FIELD_HCENTER );
      label1.setFont( Font.getDefault().derive( Font.BOLD, 18 ) );
      mainScreen.add( label1 );
      LabelField label2 = new LabelField( "Buy Missed Call Alert today.", LabelField.FIELD_HCENTER );
      label2.setFont( Font.getDefault().derive( Font.BOLD, 18 ) );
      mainScreen.add( label2 );
    }
    mainScreen.add( new LabelField( "", LabelField.FIELD_HCENTER ) );
    //#endif


    Object[] choices1 = { "Yes", "No" };
    int enableIndex = 0;
    if ( !optionProperties.getEnableAlert() )
    {
      enableIndex = 1;
    }
    enableChoice = new ObjectChoiceField( "Enable Alert: ", choices1, enableIndex );
    mainScreen.add( enableChoice );

    Object[] choices = { "30 Sec.", "1 Min.", "2 Min.", "5 Min.", "10 Min.", "15 Min." };
    frequencyUnitsChoice = new ObjectChoiceField( "Repeat alert every: ", choices, optionProperties.getFrequency() );
    mainScreen.add( frequencyUnitsChoice );
    mainScreen.add( new LabelField( "", LabelField.FIELD_HCENTER ) );
    mainScreen.add( new ProfileButton() );
    mainScreen.add( new AboutButton() );

    mainScreen.addMenuItem( _moreInfo );
    mainScreen.addMenuItem( _tellFriend );
    mainScreen.addMenuItem( _feedBack );
    mainScreen.addMenuItem( MenuItem.separator( 3000 ) );

  }

  private final class ProfileButton extends ButtonField
  {
    private ProfileButton()
    {
      super( "Sounds (Profiles)", ButtonField.CONSUME_CLICK | LabelField.USE_ALL_WIDTH | LabelField.FIELD_HCENTER );
    }

    protected void fieldChangeNotify( int context )
    {
      if ( ( context & FieldChangeListener.PROGRAMMATIC ) == 0 )
      {
        try
        {
          int moduleHandle = CodeModuleManager.getModuleHandle( "net_rim_bb_profiles_app" );
          ApplicationDescriptor[] apDes = CodeModuleManager.getApplicationDescriptors( moduleHandle );
          ApplicationManager.getApplicationManager().runApplication( apDes[0] );
        }
        catch ( ApplicationManagerException e )
        {
          // Handle exception here 
        }

      }
    }
  }

  private final class AboutButton extends ButtonField
  {
    private AboutButton()
    {
      super( "About", ButtonField.CONSUME_CLICK | LabelField.USE_ALL_WIDTH | LabelField.FIELD_HCENTER );
    }

    protected void fieldChangeNotify( int context )
    {
      if ( ( context & FieldChangeListener.PROGRAMMATIC ) == 0 )
      {
        com.mlhsoftware.AboutDialog dlg = new com.mlhsoftware.AboutDialog();
        UiApplication.getUiApplication().pushScreen( dlg );
      }
    }
  }

  public void save()
  {
    //Get the new values from the UI controls
    //and set them in optionProperties.
    optionProperties.setEnableAlert( enableChoice.getSelectedIndex() == 0 );
    optionProperties.setFrequency( frequencyUnitsChoice.getSelectedIndex() );

    //Write our changes back to the persistent store.
    optionProperties.save();

    //Null out our member variables so that their objects can be garbage
    //collected. Note that this instance continues to be held by the
    //options manager even after the user exits the options app,
    //and will be re-used next time.

    enableChoice = null;
    frequencyUnitsChoice = null;
    optionProperties = null;
  }

  private MenuItem _tellFriend = new MenuItem( "Tell a friend", 1, 10000 )
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

  private MenuItem _feedBack = new MenuItem( "Feedback", 2, 10001 )
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

  private MenuItem _moreInfo = new MenuItem( "More Info", 3, 10002 )
  {
    public void run()
    {
      Browser.getDefaultSession().displayPage( APP_URL );
    }
  };
}