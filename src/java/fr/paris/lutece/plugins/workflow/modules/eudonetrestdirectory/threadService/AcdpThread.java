package fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.threadService;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.sun.jersey.api.client.ClientResponse;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.managewferror.business.Resource;
import fr.paris.lutece.plugins.managewferror.business.service.IProcessTaskErrorService;
import fr.paris.lutece.plugins.managewferror.business.service.ProcessTaskErrorService;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business.EudonetLink;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business.EudonetLinkHome;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business.EudonetRestData;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.service.BuildJsonBodyService;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.service.EudonetClient;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.service.EudonetRestWsService;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.utils.EudonetRestException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppLogService;

public class AcdpThread extends Thread
{
    private static final String THREAD_NAME = "eudonetRest-export-Acdp-thread";
    private static final String ACTION = "ACDP ACTION";

    private EudonetClient _client;
    private EudonetRestWsService _eudonetRestWsService;
    private List<EudonetRestData> _listEuData;
    private int _nIdResource;
    private int _nIdDirectory;
    private int _nIdAction;
    private EudonetRestException _eudonetException;
    private boolean _bRunning;

    IProcessTaskErrorService _erroService = ProcessTaskErrorService.getService( );
    
    boolean bError = false;
    Resource resourceDTO= null;
    fr.paris.lutece.plugins.managewferror.business.Error error= null;

    /**
     * constructor
     * 
     * @param client
     * @param service
     * @param listEuData
     * @param idDirectory
     * @param idResource
     */
    public AcdpThread( EudonetClient client, EudonetRestWsService service, List<EudonetRestData> listEuData, int idDirectory, int idResource, int idAction )
    {
        _client = client;
        _eudonetRestWsService = service;
        _listEuData = listEuData;
        _nIdResource = idResource;
        _nIdDirectory = idDirectory;
        _nIdAction = idAction;
        setName( THREAD_NAME + _nIdResource );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void run( )
    {
       

        try
        {
        	resourceDTO = getResourceDTO( );
             error = resourceDTO.getError().get(0);
           
             
            _bRunning = true;
            _eudonetException = null;

            String strToken = token( );

            if ( strToken != null )
            {
                createRecords( strToken );
                createRecordsLink( strToken );

            }
            else
            {
                AppLogService.error( "Erreur d'authentification sur eudonet" );
                bError = true;
                error.setError( "Erreur d'authentification sur eudonet" );
                _erroService.saveErrorTrace( resourceDTO );
            }   

	        if ( !bError )
	        {
	       
	            //Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
	        	//Record record = RecordHome.findByPrimaryKey( _nIdResource, pluginDirectory );
	            _erroService.deleteErrorTrace( _nIdResource );
	        }
        }
        catch( Exception ex )
        {
            AppLogService.error( "error calling addProjectsInEudonet method : " + ex.getMessage( ), ex );
            _bRunning = false;
            bError = true;
           error.setError( "error calling addProjectsInEudonet method : " + ex.getMessage( ) );
           _erroService.saveErrorTrace( resourceDTO );
        }

        _bRunning = false;
        this.interrupt( );
    }

    public EudonetRestException getEudonetException( )
    {
        return _eudonetException;
    }

    /**
     * Return the running state
     * 
     * @return the running state
     */
    public boolean isRunning( )
    {
        return _bRunning;
    }

    /**
     * @param bool
     */
    public void setRuning( boolean bool )
    {
        this._bRunning = bool;
    }

    public String token( )
    {
        try
        {
            String strAuthenticateJson = _eudonetRestWsService.getAuthenticateJsonBody( );
            ClientResponse response = _client.getTokenAuthenticate( strAuthenticateJson );

            if ( response.getStatus( ) == 200 )
            {
                String strResponse = response.getEntity( String.class );
                JSONObject jsonObject = new JSONObject( );
                jsonObject.accumulate( "object", strResponse );

                String strStatus = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultInfos" ).getString( "Success" );

                if ( strStatus.equals( "true" ) )
                {
                    return jsonObject.getJSONObject( "object" ).getJSONObject( "ResultData" ).getString( "Token" );
                }
                else
                {
                    String strErrorMessage = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultInfos" ).getString( "ErrorMessage" );
                    AppLogService.error( "Error Eudonet : " + strErrorMessage );
                }

            }
        }
        catch( Exception ex )
        {
            AppLogService.error( "Erreur to generate the token", ex );
        }

        return null;
    }

    public void createRecords( String strToken )
    {
        if ( strToken != null )
        {
            List<Integer> idTableList = getTableListNotLink( strToken );
            for ( Integer i : idTableList )
            {
                try
                {
                    String strJsonBody = BuildJsonBodyService.getService( ).getCreateRecordJsonBody( i, _listEuData, _nIdResource, _nIdDirectory );
                    ClientResponse response = _client.createRecord( strToken, "" + i, strJsonBody );
                    if ( response.getStatus( ) == 200 )
                    {
                        String strResponse = response.getEntity( String.class );
                        JSONObject jsonObject = new JSONObject( );
                        jsonObject.accumulate( "object", strResponse );

                        String strStatus = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultInfos" ).getString( "Success" );

                        if ( strStatus.equals( "true" ) )
                        {
                            String strFileId = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultData" ).getString( "FileId" );
                            if ( strFileId != null && !strFileId.isEmpty( ) )
                            {
                                Integer nFileId = Integer.parseInt( strFileId );

                                if ( isAnnexed( i ) )
                                    createAnnexes( strToken, nFileId, i );

                                EudonetLink eudonetLink = new EudonetLink( );
                                eudonetLink.setIdRessource( _nIdResource );
                                eudonetLink.setIdField( "" + nFileId );
                                eudonetLink.setIdTable( "" + i );
                                eudonetLink.setIdTableLink( "" );

                                EudonetLinkHome.create( eudonetLink );
                            }
                            // String strErrorMessage = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultInfos" ).getString( "ErrorMessage" );
                            AppLogService.info( "Succes Creation - FileId : " + strFileId );
                        }
                        else
                        {
                            String strErrorMessage = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultInfos" ).getString( "ErrorMessage" );
                            AppLogService.error( "Error Eudonet : " + strErrorMessage );
                            bError = true;
                            error.setError("Create main Record: "+strErrorMessage);
                            _erroService.saveErrorTrace( resourceDTO );
                        }
                    }
                }
                catch( Exception ex )
                {
                    AppLogService.error( "Erreur to create table : " + i, ex );
                    bError = true;
                    error.setError("Create main Record: "+ex.getMessage( ));
                    _erroService.saveErrorTrace( resourceDTO );
                }
            }
        }
    }

    public void createRecordsLink( String strToken )
    {
        if ( strToken != null )
        {
            List<Integer> idTableList = getTableListLink( strToken );
            for ( Integer i : idTableList )
            {
                List<Integer> idTableListLinked = getTableListLinked( strToken, i );

                try
                {
                    String strJsonBody = BuildJsonBodyService.getService( ).getCreateRecordJsonBodyLink( i, _listEuData, _nIdResource, _nIdDirectory,
                            idTableListLinked );
                    ClientResponse response = _client.createRecord( strToken, "" + i, strJsonBody );
                    if ( response.getStatus( ) == 200 )
                    {
                        String strResponse = response.getEntity( String.class );
                        JSONObject jsonObject = new JSONObject( );
                        jsonObject.accumulate( "object", strResponse );

                        String strStatus = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultInfos" ).getString( "Success" );

                        if ( strStatus.equals( "true" ) )
                        {
                            String strFileId = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultData" ).getString( "FileId" );
                            if ( strFileId != null && !strFileId.isEmpty( ) )
                            {
                                Integer nFileId = Integer.parseInt( strFileId );

                                if ( isAnnexed( i ) )
                                    createAnnexes( strToken, nFileId, i );
                            }

                            AppLogService.info( "Succes Creation - FileId : " + strFileId );
                        }
                        else
                        {
                            String strErrorMessage = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultInfos" ).getString( "ErrorMessage" );
                            AppLogService.error( "Error Eudonet : " + strErrorMessage );
                            bError = true;
                            error.setError("create Record Link: "+strErrorMessage);
                            _erroService.saveErrorTrace( resourceDTO );
                        }
                    }
                }
                catch( Exception ex )
                {
                    AppLogService.error( "Erreur to create table : " + i, ex );
                    bError = true;
                    error.setError("create Record Link: "+ex.getMessage( ));
                    _erroService.saveErrorTrace( resourceDTO );
                }
            }

        }
    }

    public void createAnnexes( String strToken, int nIdFile, int nIdTable )
    {
        if ( strToken != null )
        {
            try
            {
                JSONArray jSONArray = BuildJsonBodyService.getService( ).getCreateAnnexeJsonBody( nIdFile, nIdTable, _listEuData, _nIdResource, _nIdDirectory );
                for ( int index = 0; index < jSONArray.size( ); index++ )
                {
                    JSONObject jObject = jSONArray.getJSONObject( index );
                    String strJObject = jObject.toString( );
                    ClientResponse response = _client.addAnnexes( strToken, strJObject );
                    if ( response.getStatus( ) == 200 )
                    {
                        String strResponse = response.getEntity( String.class );
                        JSONObject jsonObject = new JSONObject( );
                        jsonObject.accumulate( "object", strResponse );

                        String strStatus = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultInfos" ).getString( "Success" );

                        if ( strStatus.equals( "true" ) )
                        {
                            String strFileId = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultData" ).getString( "AnnexId" );

                            AppLogService.info( "Succes Add Annexe - FileId : " + strFileId );
                        }
                        else
                        {
                            String strErrorMessage = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultInfos" ).getString( "ErrorMessage" );
                            AppLogService.error( "Error Eudonet : adding Annexe " + strErrorMessage );
                            bError = true;
                            error.setError("Create Annexes :"+strErrorMessage);
                            _erroService.saveErrorTrace( resourceDTO );
                        }
                    }
                }
            }
            catch( Exception ex )
            {
                AppLogService.error( "Erreur to create table : " + nIdTable, ex );
                bError = true;
                error.setError("Create Annexes :"+ex.getMessage());
                _erroService.saveErrorTrace( resourceDTO );
            }
        }
    }

    public List<Integer> getTableListDistinct( )
    {
        List<Integer> idTableList = new ArrayList<Integer>( );
        for ( EudonetRestData eudonetRestData : _listEuData )
        {
            String strIdTable = eudonetRestData.getIdTable( ).split( "-" ) [0];
            if ( !strIdTable.isEmpty( ) )
            {
                Integer nIdTable = Integer.parseInt( strIdTable );
                if ( !strIdTable.equals( "102000" ) && !idTableList.contains( nIdTable ) )
                {
                    idTableList.add( nIdTable );
                }
            }
        }

        return idTableList;
    }

    public boolean isAnnexed( Integer nIdTable )
    {
        for ( EudonetRestData eudonetRestData : _listEuData )
        {
            String strIdTableLink = eudonetRestData.getIdTableLink( ).split( "-" ) [0];
            String strIdTable = eudonetRestData.getIdTable( ).split( "-" ) [0];
            if ( !strIdTableLink.isEmpty( ) && strIdTable.equals( "" + nIdTable ) )
            {
                return true;
            }
        }

        return false;
    }

    public List<Integer> getTableListNotLink( String strToken )
    {
        List<Integer> idTableListDistinct = getTableListDistinct( );
        List<Integer> idTableList = new ArrayList<Integer>( );
        for ( Integer idTable : idTableListDistinct )
        {
            String strBody = BuildJsonBodyService.getService( ).getMetaInfosJsonBody( "" + idTable );
            if ( strBody != null )
            {
                ClientResponse response = _client.getAttributListMetaInfos( strToken, strBody );
                if ( response.getStatus( ) == 200 )
                {
                    String strResponse = response.getEntity( String.class );
                    JSONObject jsonObject = new JSONObject( );
                    jsonObject.accumulate( "object", strResponse );
                    String strStatus = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultInfos" ).getString( "Success" );
                    if ( strStatus.equals( "true" ) )
                    {
                        JSONArray jsonArray = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultMetaData" ).getJSONArray( "Tables" ).getJSONObject( 0 )
                                .getJSONArray( "Links" );
                        if ( jsonArray.size( ) == 0 && idTableListDistinct.contains( idTable ) )
                        {
                            idTableList.add( idTable );
                        }
                    }
                    else
                    {
                        String strErrorMessage = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultInfos" ).getString( "ErrorMessage" );
                        AppLogService.error( "Error Eudonet : " + strErrorMessage );
                    }
                }
            }
        }

        return idTableList;
    }

    public List<Integer> getTableListLinked( String strToken, Integer nIdTable )
    {
        List<Integer> idTableList = new ArrayList<Integer>( );
        String strBody = BuildJsonBodyService.getService( ).getMetaInfosJsonBody( "" + nIdTable );
        if ( strBody != null )
        {
            ClientResponse response = _client.getAttributListMetaInfos( strToken, strBody );
            if ( response.getStatus( ) == 200 )
            {
                String strResponse = response.getEntity( String.class );
                JSONObject jsonObject = new JSONObject( );
                jsonObject.accumulate( "object", strResponse );
                String strStatus = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultInfos" ).getString( "Success" );
                if ( strStatus.equals( "true" ) )
                {
                    JSONArray jsonArray = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultMetaData" ).getJSONArray( "Tables" ).getJSONObject( 0 )
                            .getJSONArray( "Links" );
                    for ( int i = 0; i < jsonArray.size( ); i++ )
                    {
                        idTableList.add( jsonArray.getInt( i ) );
                    }
                }
                else
                {
                    String strErrorMessage = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultInfos" ).getString( "ErrorMessage" );
                    AppLogService.error( "Error Eudonet : " + strErrorMessage );
                }
            }
        }

        return idTableList;
    }

    public List<Integer> getTableListLink( String strToken )
    {
        List<Integer> idTableListDistinct = getTableListDistinct( );
        List<Integer> idTableListNotLink = getTableListNotLink( strToken );
        for ( Integer i : idTableListNotLink )
        {
            if ( idTableListDistinct.contains( i ) )
            {
                idTableListDistinct.remove( i );
            }
        }

        return idTableListDistinct;
    }

    public Resource getResourceDTO( )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        Record record = RecordHome.findByPrimaryKey( _nIdResource, pluginDirectory );
        Directory directory= DirectoryHome.findByPrimaryKey(record.getDirectory().getIdDirectory(), pluginDirectory);
        Resource resourceDTO = new Resource( );
        resourceDTO.setIdResource( record.getIdRecord( ) );
        //resourceDTO.setIdGfaProvider( Integer.parseInt( record.getDirectory( ).getIdDirectory( ) + "" ) );
        resourceDTO.setAction( _nIdAction );
        resourceDTO.setDescription( ACTION );
        resourceDTO.setStatus( Resource.STATUS_KO );
        resourceDTO.setIdWorkflow(directory.getIdWorkflow( ));

        List<fr.paris.lutece.plugins.managewferror.business.Error> listError = new ArrayList<fr.paris.lutece.plugins.managewferror.business.Error>( );
        fr.paris.lutece.plugins.managewferror.business.Error error = new fr.paris.lutece.plugins.managewferror.business.Error( );
        java.sql.Timestamp date = new java.sql.Timestamp( System.currentTimeMillis( ) );
        error.setDateError( date );
        error.setAction( ACTION );
        listError.add( error );
        resourceDTO.setError( listError );

        return resourceDTO;
    }
}
