package fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.File;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.PhysicalFile;
import fr.paris.lutece.plugins.directory.business.PhysicalFileHome;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business.EudonetLink;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business.EudonetLinkHome;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business.EudonetRestData;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class BuildJsonBodyService
{
    private static BuildJsonBodyService _singleton;
    private static final String PROPERTY_ENTRY_TYPE_GEOLOCATION = "directory.entry_type.geolocation";
    private static final String PROPERTY_ENTRY_TYPE_IMAGE = "directory.resource_rss.entry_type_image";

    // Constants
    private static final int CONSTANT_ENTRY_DATE_CREATION = -2;
    private static final int CONSTANT_ENTRY_DATE_MODIFICATION = -3;

    /**
     * The plugin directory.
     */
    private final Plugin _pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

    /**
     * @return instance BuildJsonBodyService
     */
    public static BuildJsonBodyService getService( )
    {
        if ( _singleton == null )
        {
            _singleton = new BuildJsonBodyService( );

            return _singleton;
        }

        return _singleton;
    }

    /**
     * get Entry list of directory
     * 
     * @param nidDirectory
     * @return Entry list of directory
     */
    public List<IEntry> getListEntries( int nidDirectory )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        List<IEntry> listEntries = new ArrayList<IEntry>( );
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdDirectory( nidDirectory );

        listEntries = EntryHome.getEntryList( entryFilter, pluginDirectory );

        return listEntries;
    }

    /**
     * get record field value
     * 
     * @param nIdEntry
     * @param nIdRecord
     * @param nIdDirectory
     * @return record field value
     */
    public String getRecordFieldValue( int nIdEntry, int nIdRecord, int nIdDirectory )
    {
        String strRecordFieldValue = StringUtils.EMPTY;
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        if ( nIdEntry == CONSTANT_ENTRY_DATE_CREATION || nIdEntry == CONSTANT_ENTRY_DATE_MODIFICATION )
        {
            if ( nIdEntry == CONSTANT_ENTRY_DATE_CREATION )
            {
                return getDateCreation( nIdRecord );
            }
            else
            {
                return getDateModification( nIdRecord );
            }
        }

        IEntry entry = EntryHome.findByPrimaryKey( nIdEntry, pluginDirectory );

        if ( ( entry != null ) )
        {
            RecordFieldFilter recordFieldFilter = new RecordFieldFilter( );
            recordFieldFilter.setIdDirectory( nIdDirectory );
            recordFieldFilter.setIdEntry( entry.getIdEntry( ) );
            recordFieldFilter.setIdRecord( nIdRecord );

            List<RecordField> listRecordFields = RecordFieldHome.getRecordFieldList( recordFieldFilter, pluginDirectory );

            if ( entry.getEntryType( ).getIdType( ) == AppPropertiesService.getPropertyInt( PROPERTY_ENTRY_TYPE_GEOLOCATION, 16 ) )
            {
                if ( listRecordFields.size( ) >= 4 )
                {
                    return listRecordFields.get( 2 ).getValue( ) + ", " + listRecordFields.get( 3 ).getValue( );
                }
                else
                {
                    return StringUtils.EMPTY;
                }
            }

            if ( entry.getEntryType( ).getIdType( ) == 8 )
            {
                RecordField recordFieldIdDemand = listRecordFields.get( 0 );
                strRecordFieldValue = recordFieldIdDemand.getValue( );

                if ( recordFieldIdDemand.getEntry( ) != null )
                {
                    return recordFieldIdDemand.getEntry( ).getTitle( );
                }
            }

            if ( entry.getEntryType( ).getIdType( ) == 4 )
            {
                RecordField recordFieldIdDemand = listRecordFields.get( 0 );
                strRecordFieldValue = recordFieldIdDemand.getValue( );

                if ( recordFieldIdDemand.getField( ) != null )
                {
                    strRecordFieldValue = recordFieldIdDemand.getField( ).getTitle( );
                }
                try
                {
                    long times = Long.parseLong( strRecordFieldValue );
                    Date date = new Date( times );
                    DateFormat sdf = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
                    String strDate = sdf.format( date );
                    if ( strDate != null )
                        return strDate;

                }
                catch( Exception e )
                {
                    // ("NumberFormatException: " + nfe.getMessage());
                }
            }

            if ( ( listRecordFields != null ) && !listRecordFields.isEmpty( ) && ( listRecordFields.get( 0 ) != null ) )
            {
                RecordField recordFieldIdDemand = listRecordFields.get( 0 );
                strRecordFieldValue = recordFieldIdDemand.getValue( );

                if ( recordFieldIdDemand.getField( ) != null )
                {
                    strRecordFieldValue = recordFieldIdDemand.getField( ).getTitle( );
                }
            }
        }

        return strRecordFieldValue;
    }

    /**
     * get record field value
     * 
     * @param nIdEntry
     * @param nIdRecord
     * @param nIdDirectory
     * @return record field value
     */
    public File getRecordFileValue( int nIdEntry, int nIdRecord, int nIdDirectory )
    {
        String strRecordFieldValue = StringUtils.EMPTY;
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        IEntry entry = EntryHome.findByPrimaryKey( nIdEntry, pluginDirectory );

        if ( ( entry != null ) )
        {
            RecordFieldFilter recordFieldFilter = new RecordFieldFilter( );
            recordFieldFilter.setIdDirectory( nIdDirectory );
            recordFieldFilter.setIdEntry( entry.getIdEntry( ) );
            recordFieldFilter.setIdRecord( nIdRecord );

            List<RecordField> listRecordFields = RecordFieldHome.getRecordFieldList( recordFieldFilter, pluginDirectory );

            if ( entry.getEntryType( ).getIdType( ) == 8 )
            {
                RecordField recordFieldIdDemand = listRecordFields.get( 0 );
                strRecordFieldValue = recordFieldIdDemand.getValue( );

                if ( recordFieldIdDemand.getFile( ) != null )
                {
                    return recordFieldIdDemand.getFile( );
                }
            }
        }

        return null;
    }

    /**
     * get Identifiant of the record
     * 
     * @param nIdResource
     * @return id Record
     */
    public int getRecord( int nIdResource )
    {
        Record record = RecordHome.findByPrimaryKey( nIdResource, _pluginDirectory );

        return record.getIdRecord( );
    }

    private Timestamp getDateCreation( int nIdRecord, int nIdDirectory )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        RecordFieldFilter recordFieldFilter = new RecordFieldFilter( );
        recordFieldFilter.setIdDirectory( nIdDirectory );
        recordFieldFilter.setIdRecord( nIdRecord );

        List<RecordField> listRecordFields = RecordFieldHome.getRecordFieldList( recordFieldFilter, pluginDirectory );

        if ( ( listRecordFields != null ) && !listRecordFields.isEmpty( ) && ( listRecordFields.get( 0 ) != null ) )
        {
            return listRecordFields.get( 0 ).getRecord( ).getDateCreation( );
        }

        return null;
    }

    private String getDateCreation( int nIdRecord )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        Record record = RecordHome.findByPrimaryKey( nIdRecord, pluginDirectory );
        if ( record != null )
        {
            long times = record.getDateCreation( ).getTime( );
            Date date = new Date( times );
            DateFormat sdf = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
            String strDate = sdf.format( date );
            if ( strDate != null )
                return strDate;
        }

        return "";
    }

    private String getDateModification( int nIdRecord )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        Record record = RecordHome.findByPrimaryKey( nIdRecord, pluginDirectory );
        if ( record != null )
        {
            long times = record.getDateModification( ).getTime( );
            Date date = new Date( times );
            DateFormat sdf = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
            String strDate = sdf.format( date );
            if ( strDate != null )
                return strDate;
        }

        return "";
    }

    public String getCreateRecordJsonBody( int nIdTable, List<EudonetRestData> _entries, int nIdRessource, int nIdDirectory )
    {
        JSONObject jsonObjectFinal = new JSONObject( );
        JSONArray jsonArray = new JSONArray( );

        for ( EudonetRestData entry : _entries )
        {
            String strIdTable = entry.getIdTable( ).split( "-" ) [0];

            if ( strIdTable.equals( "" + nIdTable ) )
            {
                String strIdAtt = entry.getIdAttribut( ).split( "-" ) [0];
                JSONObject jsonObject = new JSONObject( );
                jsonObject.accumulate( "DescId", Integer.parseInt( strIdAtt ) );

                jsonObject.accumulate( "Value", getRecordFieldValue( entry.getOrderEntry( ), nIdRessource, nIdDirectory ) );

                jsonArray.add( jsonObject );
            }
        }

        jsonObjectFinal.accumulate( "Fields", jsonArray );

        return jsonObjectFinal.toString( );
    }

    public String getCreateRecordJsonBodyLink( int nIdTable, List<EudonetRestData> _entries, int nIdRessource, int nIdDirectory, List<Integer> listTableLinked )
    {
        JSONObject jsonObjectFinal = new JSONObject( );
        JSONArray jsonArray = new JSONArray( );

        for ( EudonetRestData entry : _entries )
        {
            String strIdTable = entry.getIdTable( ).split( "-" ) [0];

            if ( strIdTable.equals( "" + nIdTable ) )
            {
                String strIdAtt = entry.getIdAttribut( ).split( "-" ) [0];
                JSONObject jsonObject = new JSONObject( );

                jsonObject.accumulate( "DescId", Integer.parseInt( strIdAtt ) );
                jsonObject.accumulate( "Value", getRecordFieldValue( entry.getOrderEntry( ), nIdRessource, nIdDirectory ) );

                jsonArray.add( jsonObject );
            }
        }

        for ( Integer i : listTableLinked )
        {
            EudonetLink eudonetLink = EudonetLinkHome.findBy( nIdRessource, i );
            JSONObject jsonObject = new JSONObject( );

            jsonObject.accumulate( "DescId", eudonetLink.getIdTable( ) );
            jsonObject.accumulate( "Value", "" + eudonetLink.getIdField( ) );

            jsonArray.add( jsonObject );
        }

        jsonObjectFinal.accumulate( "Fields", jsonArray );

        return jsonObjectFinal.toString( );
    }

    public JSONArray getCreateAnnexeJsonBody( int nIdFile, int nIdTable, List<EudonetRestData> _entries, int nIdRessource, int nIdDirectory )
    {
        JSONArray jsonArray = new JSONArray( );

        for ( EudonetRestData entry : _entries )
        {
            String strIdTableLink = entry.getIdTableLink( ).split( "-" ) [0];
            String strIdTable = entry.getIdTable( ).split( "-" ) [0];
            if ( !strIdTableLink.isEmpty( ) && strIdTable.equals( "" + nIdTable ) )
            {
                File file = getRecordFileValue( entry.getOrderEntry( ), nIdRessource, nIdDirectory );
                if ( file != null )
                {
                    PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( file.getPhysicalFile( ).getIdPhysicalFile( ), _pluginDirectory );
                    String strFileName = file.getTitle( );
                    String strContent = "";
                    if ( physicalFile != null && physicalFile.getValue( ) != null )
                    {
                        byte [ ] bytes = physicalFile.getValue( );
                        byte [ ] encoded = Base64.encodeBase64( bytes );
                        strContent = new String( encoded );
                    }

                    JSONObject jsonObject = new JSONObject( );
                    jsonObject.accumulate( "FileId", nIdFile );
                    jsonObject.accumulate( "TabId", nIdTable );
                    jsonObject.accumulate( "FileName", strFileName );
                    jsonObject.accumulate( "Content", strContent );
                    jsonObject.accumulate( "IsUrl", false );

                    jsonArray.add( jsonObject );
                }
            }
        }

        return jsonArray;
    }

    public String getMetaInfosJsonBody( String strIdTable )
    {
        if ( strIdTable != null )
        {
            JSONObject jsonObject = new JSONObject( );
            JSONArray jsonArray = new JSONArray( );
            JSONObject jsonElement = new JSONObject( );
            JSONArray jsonElementArray = new JSONArray( );

            jsonElementArray.add( 0 );

            jsonElement.accumulate( "DescId", Integer.parseInt( strIdTable ) );
            jsonElement.accumulate( "AllFields", true );
            jsonElement.accumulate( "Fields", jsonElementArray );

            jsonArray.add( jsonElement );

            jsonObject.accumulate( "Tables", jsonArray );

            return jsonObject.toString( );
        }

        return null;
    }
}
