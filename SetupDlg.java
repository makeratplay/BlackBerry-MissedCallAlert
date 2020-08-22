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

import net.rim.device.api.ui.MenuItem;

import java.util.*;

import net.rim.blackberry.api.phone.phonelogs.PhoneCallLogID;
import net.rim.blackberry.api.phone.phonelogs.PhoneCallLog;
import net.rim.blackberry.api.phone.phonelogs.PhoneCallLogID;
import net.rim.blackberry.api.phone.phonelogs.PhoneLogs;

import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationManagerException;

class SetupDlg extends PopupScreen
{
  SetupDlg( Manager delegate )
  {
    super( delegate );


    add( new NullField( Field.FOCUSABLE ) );
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
      LabelField label1 = new LabelField( "You have " + daysLeft + " days left in your trial", LabelField.FIELD_HCENTER );
      //label1.setFont( Font.getDefault().derive(Font.PLAIN, 14) );
      add( label1 );
      //#endif

      add( new LabelField( "", LabelField.FIELD_HCENTER ) );
      RichTextField label2 = new RichTextField( "To configure the alert sound, select 'Sounds' or 'Profiles' icon from your home screen. (Note: By default the alert is silent)", Field.FIELD_HCENTER | Field.FOCUSABLE );
      //label2.setFont( Font.getDefault().derive(Font.PLAIN, 14) );
      add( label2 );
      
      add( new NullField( Field.FOCUSABLE ) );
      add( new LabelField( "", LabelField.FIELD_HCENTER ) );
      RichTextField label3 = new RichTextField( "Missed Call Alert does not create an icon. To configure, select the Options icon from your home screen.", Field.FIELD_HCENTER | Field.FOCUSABLE );
      //label3.setFont( Font.getDefault().derive(Font.PLAIN, 14) );
      add( label3 );

      add( new NullField( Field.FOCUSABLE ) );
      add( new LabelField( "", LabelField.FIELD_HCENTER ) );
      RichTextField label4 = new RichTextField( "For more information visit www.MLHSoftware.com", Field.FIELD_HCENTER | Field.FOCUSABLE );
      //label4.setFont( Font.getDefault().derive(Font.PLAIN, 14) );
      add( label4 );
    }
    else
    {
      add( new LabelField( "", LabelField.FIELD_HCENTER ) );
      LabelField label5 = new LabelField( "Free trial has ended.", LabelField.FIELD_HCENTER );
      //label5.setFont( Font.getDefault().derive(Font.BOLD, 20) );
      add( label5 );
      LabelField label6 = new LabelField( "Buy Missed Call Alert today.", LabelField.FIELD_HCENTER );
      //label6.setFont( Font.getDefault().derive(Font.BOLD, 20) );
      add( label6 );
    }



    add( new LabelField( "", LabelField.FIELD_HCENTER ) );

    

    HorizontalFieldManager hfm = new HorizontalFieldManager( Field.FIELD_HCENTER | Field.FIELD_BOTTOM );
    hfm.add( new OkButton() );
    add( hfm );
  }

 

  public void CloseDlg()
  {
    clearAlert();
    close();

    //UiApplication.getUiApplication().requestForeground();
    System.exit( 1 );
  }

  private final class ProfileButton extends ButtonField
  {
    private ProfileButton()
    {
      super( "Sounds", ButtonField.CONSUME_CLICK | LabelField.USE_ALL_WIDTH | LabelField.FIELD_HCENTER );
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

  private final class TestButton extends ButtonField
  {
    private TestButton()
    {
      super( "Test", ButtonField.CONSUME_CLICK | LabelField.USE_ALL_WIDTH | LabelField.FIELD_HCENTER );
    }

    protected void fieldChangeNotify( int context )
    {
      if ( ( context & FieldChangeListener.PROGRAMMATIC ) == 0 )
      {
        soundAlert();
      }
    }
  }



  public void soundAlert()
  {
    NotificationsManager.triggerImmediateEvent( MissedCallAlert.NOTIFICATIONS_ID_1, 0, this, null );
  }

  public void clearAlert()
  {
    NotificationsManager.cancelImmediateEvent( MissedCallAlert.NOTIFICATIONS_ID_1, 0, this, null );
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

}
