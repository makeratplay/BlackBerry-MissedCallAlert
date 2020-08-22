//#preprocess

//#ifdef TRIAL
package com.mlhsoftware.MissedCallAlertDemo;
//#else
package com.mlhsoftware.MissedCallAlert;
//#endif

import net.rim.device.api.ui.container.*;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.ui.*;

import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.notification.NotificationsConstants;
import net.rim.device.api.notification.NotificationsManager;

import net.rim.blackberry.api.options.*;

import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.applicationcontrol.ApplicationPermissions;

import net.rim.device.api.system.CodeModuleManager;

import net.rim.device.api.system.EventInjector;
import net.rim.device.api.system.*;

import net.rim.device.api.system.EventLogger;

public class MissedCallAlert extends Application
{  
  //#ifdef TRIAL
  public static final long NOTIFICATIONS_ID_1 = 0xb6df649faf0b35e9L; //com.mlhsoftware.MissedCallAlert.NOTIFICATIONS_ID_DEMO
  //#else
  public static final long NOTIFICATIONS_ID_1 = 0x90860e1cd548734dL; //com.mlhsoftware.MissedCallAlert.NOTIFICATIONS_ID_1
  //#endif

  //#ifdef TRIAL
  public static final String logAppName = "Missed Call Alert Demo";
  public static final long LOGGER_ID = 0xfed78a1ab9668e78L; //com.mlhsoftware.MissedCallAlert.loggerid
  //#else
  public static final String logAppName = "Missed Call Alert";
  public static final long LOGGER_ID = 0xf44c8278120f7fa4L; //com.mlhsoftware.MissedCallAlert.loggeriddemo
  //#endif


  
  

  private static MissedCallAlert m_theApp = null;
  private static MCAPhoneListener m_phoneListener = null;
  private static AlertDlg m_dlg = null;
  private static boolean m_exitOnClose = false;

  public static void main( String[] args )
  {
    EventLogger.register( LOGGER_ID, logAppName, EventLogger.VIEWER_STRING );

   // if ( args.length == 1 && args[0].equals( "autostartup" ) )
    {
      EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Started".getBytes(), EventLogger.ALWAYS_LOG );
      // Create and register the object that will listen for Phone events.  Check for ControlledAccessException

      //#ifdef TRIALl
      if ( isMCAInstalled() )
      {
        // don't run if full version is installed
        EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Demo stopped. Full version present".getBytes(), EventLogger.ALWAYS_LOG );
        return;
      }
      //#endif

      boolean bExpiredTrial = false;

      //#ifdef TRIAL
      MCAOptionsProperties optionProperties = MCAOptionsProperties.fetch();
      int daysLeft = optionProperties.getDaysLeft();
      if ( daysLeft < 1 )
      {
        EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Demo Expired".getBytes(), EventLogger.ALWAYS_LOG );
        bExpiredTrial = true;
      }
      //#endif

      if ( !bExpiredTrial )
      {
        ApplicationPermissionsManager appPermissionsManager = ApplicationPermissionsManager.getInstance();
        if ( appPermissionsManager != null )
        {
          ApplicationPermissions currentPermissions = appPermissionsManager.getApplicationPermissions();
          if ( currentPermissions != null )
          {
            int phonePermission = currentPermissions.getPermission( ApplicationPermissions.PERMISSION_PHONE );
            if ( phonePermission == ApplicationPermissions.VALUE_DENY )
            {
              EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Permissions Request (VALUE_DENY)".getBytes(), EventLogger.ALWAYS_LOG );
              ApplicationPermissions requestedPermissions = new ApplicationPermissions();
              requestedPermissions.addPermission( ApplicationPermissions.PERMISSION_PHONE );
              appPermissionsManager.invokePermissionsRequest( requestedPermissions );
            }
            else if ( phonePermission != ApplicationPermissions.VALUE_ALLOW )
            {
              if ( MCAOptionsProperties.isFirstRun() )
              {
                EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Permissions Request (!VALUE_ALLOW)".getBytes(), EventLogger.ALWAYS_LOG );
                ApplicationPermissions requestedPermissions = new ApplicationPermissions();
                requestedPermissions.addPermission( ApplicationPermissions.PERMISSION_PHONE );
                appPermissionsManager.invokePermissionsRequest( requestedPermissions );
              }
            }
          }
        }
      }

      try
      {
        if ( !bExpiredTrial )
        {
          MCAOptionsProvider provider = new MCAOptionsProvider();
          OptionsManager.registerOptionsProvider( provider );
          EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Register Options Provider".getBytes(), EventLogger.ALWAYS_LOG );

          m_theApp.registerNotificationObjects();

          m_phoneListener = new MCAPhoneListener();
          Phone.addPhoneListener( m_phoneListener );
          EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Add Phone Listener".getBytes(), EventLogger.ALWAYS_LOG );
        }

        if ( MCAOptionsProperties.isFirstRun() || bExpiredTrial )
        {
          m_theApp = new MissedCallAlert();
          if ( bExpiredTrial )
          {
            m_theApp.missedCall( "", "" );
          }
          else
          {
            m_theApp.displaySetuDlg();
          }

          m_theApp.enterEventDispatcher();
        }

      }
      catch ( ControlledAccessException e )
      {
        String msg = "Access to Phone API restricted. You need to correct the Permissions and reboot your phone. ";
        EventLogger.logEvent( MissedCallAlert.LOGGER_ID, msg.getBytes(), EventLogger.SEVERE_ERROR );
        m_theApp = new MissedCallAlert();
        m_theApp.missedCall( "", msg );
        m_theApp.enterEventDispatcher();
      }
    }
  }

  public MissedCallAlert()
  {
    m_exitOnClose = true;
  }


  private static void registerNotificationObjects()
  {
    try
    {
      //New Notifications Sources - these will show up as editable configurations in the Profiles application
      NotificationsManager.registerSource( NOTIFICATIONS_ID_1, new Object()
          {
            public String toString()
            {
              //#ifdef TRIAL
              return "Missed Call Alert Demo";
              //#else
              return "Missed Call Alert";
              //#endif
            }
          }
          , NotificationsConstants.IMPORTANT
      );

      NotificationsManager.unHideSource( NOTIFICATIONS_ID_1 );
      EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Register Notification Objects".getBytes(), EventLogger.ALWAYS_LOG );
    }
    catch ( Exception e )
    {
      String msg;
      msg = "Failed to register notification source: " + e.toString();
      EventLogger.logEvent( MissedCallAlert.LOGGER_ID, msg.getBytes(), EventLogger.SEVERE_ERROR );
      System.out.println( "MissedCallAlert: " + msg );
      Dialog.alert( msg );
    }
  }

  public static void missedCall( String phoneNumber, String errorText )
  {
    //#ifdef TRIAL
    if ( isMCAInstalled() )
    {
      // don't run if full version is installed
      return;
    }
    //#endif

    MCAOptionsProperties optionProperties = MCAOptionsProperties.fetch();
    if ( optionProperties.getEnableAlert() )
    {
      if ( m_dlg == null )
      {
        int seconds = optionProperties.getSeconds();
        UiEngine ui = Ui.getUiEngine();

        VerticalFieldManager manager = new VerticalFieldManager();
        m_dlg = new AlertDlg( manager, phoneNumber, errorText, seconds );

        ui.pushGlobalScreen( m_dlg, -1073741823, 0 ); //UiEngine.GLOBAL_SHOW_LOWER
        EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Alert dlg displayed".getBytes(), EventLogger.DEBUG_INFO );
      }
      else
      {
        m_dlg.incCallCount();
      }
    }
    else
    {
      EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "MCA disabled".getBytes(), EventLogger.ALWAYS_LOG );
    }
  }

  public void displaySetuDlg()
  {
    UiEngine ui = Ui.getUiEngine();
    VerticalFieldManager manager = new VerticalFieldManager( VerticalFieldManager.VERTICAL_SCROLL | VerticalFieldManager.VERTICAL_SCROLLBAR );
    SetupDlg dlg = new SetupDlg( manager );
    ui.pushGlobalScreen( dlg, 1, UiEngine.GLOBAL_SHOW_LOWER );
    EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Setup dlg displayed".getBytes(), EventLogger.DEBUG_INFO );
  }


  public static void dlgDismissed()
  {
    EventLogger.logEvent( MissedCallAlert.LOGGER_ID, "Alert dlg dismissed".getBytes(), EventLogger.DEBUG_INFO );
    m_dlg = null;
    if ( m_exitOnClose )
    {
      System.exit( 1 );
    }
  }

  public static boolean isMCAInstalled()
  {
    boolean retVal = false;
    int[] handles = CodeModuleManager.getModuleHandles();
    for ( int i = 0; i < handles.length; i++ )
    {
      String moduleName = CodeModuleManager.getModuleName( handles[i] );
      if ( moduleName.startsWith( "MissedCallAlert" ) )
      {
        if ( !moduleName.startsWith( "MissedCallAlertDemo" ) )
        {
          retVal = true;
          break;
        }
      }
    }
    return retVal;
  }
}
