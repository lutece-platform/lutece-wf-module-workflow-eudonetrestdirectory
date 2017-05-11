package fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.service.daemon;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business.EudonetLink;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business.EudonetLinkHome;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;

public class PurgeResourceDaemon extends Daemon
{
    @Override
    public void run( )
    {
        deleteResource( );
    }

    /**
     * get the eligible records to delete
     * 
     * @return List<Record>
     */
    public List<Record> getEligibleRecordsToDelete( )
    {
        List<Record> recordsToDeleteList = new ArrayList<Record>( );
        Date today = new Date( );
        Timestamp lastWeek = new Timestamp( today.getTime( ) - 3600 * 1000 * 24 * 7 );
        // Timestamp lastWeek = new Timestamp(today.getTime() );

        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        RecordFieldFilter recordFieldFilter = new RecordFieldFilter( );

        List<Record> recordList = RecordHome.getListRecord( recordFieldFilter, pluginDirectory );
        for ( Record record : recordList )
        {
            if ( record.getDateModification( ).before( lastWeek ) )
            {
                recordsToDeleteList.add( record );
            }
        }

        return recordsToDeleteList;
    }

    /**
     * get the records to delete
     * 
     * @return List<Record>
     */
    public List<Record> getRecordsToDelete( )
    {
        List<Record> recordsToDeleteList = new ArrayList<Record>( );
        List<Record> recordList = getEligibleRecordsToDelete( );
        for ( Record record : recordList )
        {
            List<EudonetLink> eudonetLinkList = EudonetLinkHome.findAll( record.getIdRecord( ) );
            if ( eudonetLinkList.size( ) > 0 )
            {
                recordsToDeleteList.add( record );
            }
        }

        return recordsToDeleteList;
    }

    /**
     * get the EudonetLinks to delete
     * 
     * @return List<EudonetLink>
     */
    public List<EudonetLink> getEudonetLinksToDelete( )
    {
        List<EudonetLink> eudonetLinksToDeleteList = new ArrayList<EudonetLink>( );
        List<Record> recordList = getEligibleRecordsToDelete( );
        for ( Record record : recordList )
        {
            List<EudonetLink> eudonetLinkList = EudonetLinkHome.findAll( record.getIdRecord( ) );
            if ( eudonetLinkList.size( ) > 0 )
            {
                eudonetLinksToDeleteList.addAll( eudonetLinkList );
            }
        }

        return eudonetLinksToDeleteList;
    }

    /**
     * delete all resources
     */
    public void deleteResource( )
    {
        List<Record> recordsToDelete = getRecordsToDelete( );
        List<EudonetLink> eudonetLinksToDelete = getEudonetLinksToDelete( );

        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        for ( Record record : recordsToDelete )
        {
            int nIdRecord = record.getIdRecord( );
            RecordHome.remove( nIdRecord, pluginDirectory );
        }

        for ( EudonetLink eudo : eudonetLinksToDelete )
        {
            int nIdEudo = eudo.getId( );
            EudonetLinkHome.delete( nIdEudo );
        }
    }
}
