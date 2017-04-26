/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.web;

import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business.EudonetRestData;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business.TaskEudonetRestConfig;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business.TaskEudonetRestConfigHome;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.service.EudonetClient;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.utils.EudonetRestDirctoryConstants;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.sun.jersey.api.client.ClientResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * EudonetTaskComponent
 *
 */
public class EudonetRestTaskComponent extends NoFormTaskComponent
{
    // MARKS
    private static final String MARKER_TASK_EUDONET_CONFIG = "taskConfig";
    private static final String MARKER_LIST_ATTRIBUT_EUDONET = "list_attribut";
    private static final String MARKER_LIST_TABLE_EUDONET = "list_tableEudonet";
    private static final String MARKER_LIST_TABLE_EUDONET_LINK = "list_tableEudonet_link";
    private static final String MARKER_ENTRY = "entries";

    // TEMPLATES
    private static final String TEMPLATE_TASK_EUDONET = "admin/plugins/workflow/modules/eudonetrestdirectory/task_export_eudonet_config.html";

    // PARAMETERS
    public static final String PARAMETER_ID_DIRECTORE = "id_directory";
    public static final String PARAMETER_ID_TELESERVICE = "id_teleservice";
    public static final String PARAMETER_ID_TABLE_EUDONET = "id_tableEudonet";
    public static final String PARAMETER_ID_TABLE_EUDONET_LINK = "id_tableEudonet_link";
    public static final String PARAMETER_BASE_URL = "base_url";
    public static final String PARAMETER_SUBSCRIBER_LOGIN = "subscriber_login";
    public static final String PARAMETER_SUBSCRIBER_PASSWORD = "subscriber_password";
    public static final String PARAMETER_BASE_NAME = "base_name";
    public static final String PARAMETER_USER_LOGIN = "user_login";
    public static final String PARAMETER_USER_PASSWORD = "user_password";
    public static final String PARAMETER_USER_LANG = "user_lang";
    public static final String PARAMETER_PRODUCT_NAME = "product_name";
    public static final String PARAMETER_CREAT_ENTRY = "apply";
    public static final String PARAMETER_DELETE_ENTRY = "deleteEntry";
    public static final String PARAMETER_CREATE_ENTRY = "createEntry";
    public static final String PARAMETER_ORDER_ENTRY = "order_entry";
    public static final String PARAMETER_EUDONET_ATTRIBUT = "eudonet_attribut";
    public static final String PARAMETER_EUDONET_TABLE = "eudonet_table";
    public static final String PARAMETER_ID_ENTRY = "id_entry";

    // SERVICES
    @Inject
    @Named( EudonetRestDirctoryConstants.BEAN_EUDONET_DIRECTORY_CONFIG_SERVICE )
    private ITaskConfigService _taskEudonetConfigService;

    private EudonetClient _client;
    private TaskEudonetRestConfig _config;

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        String idDirectory = request.getParameter( PARAMETER_ID_DIRECTORE );
        String idTableEudonet = request.getParameter( PARAMETER_ID_TABLE_EUDONET ) == null ? "-1" : request.getParameter( PARAMETER_ID_TABLE_EUDONET );
        String baseUrl = request.getParameter( PARAMETER_BASE_URL );
        String subscriberLogin = request.getParameter( PARAMETER_SUBSCRIBER_LOGIN );
        String subscriberPassword = request.getParameter( PARAMETER_SUBSCRIBER_PASSWORD );
        String baseName = request.getParameter( PARAMETER_BASE_NAME );
        String userLogin = request.getParameter( PARAMETER_USER_LOGIN );
        String userPassword = request.getParameter( PARAMETER_USER_PASSWORD );
        String userLang = request.getParameter( PARAMETER_USER_LANG );
        String productName = request.getParameter( PARAMETER_PRODUCT_NAME );

        _config = _taskEudonetConfigService.findByPrimaryKey( task.getId( ) );
        Boolean bCreate = false;

        if ( _config == null )
        {
            _config = new TaskEudonetRestConfig( );
            _config.setIdTask( task.getId( ) );
            bCreate = true;
        }

        if ( ( ( request.getParameter( PARAMETER_CREAT_ENTRY ) != null ) && PARAMETER_CREATE_ENTRY.equals( request.getParameter( PARAMETER_CREAT_ENTRY ) ) )
                && ( Integer.parseInt( idDirectory ) != WorkflowUtils.CONSTANT_ID_NULL ) )
        {
            String ordreEntry = request.getParameter( PARAMETER_ORDER_ENTRY ).equals( "" ) ? "-1" : request.getParameter( PARAMETER_ORDER_ENTRY );
            String eudonetAttribut = request.getParameter( PARAMETER_EUDONET_ATTRIBUT );
            String eudonetTable = request.getParameter( PARAMETER_ID_TABLE_EUDONET );
            String eudonetTableLink = request.getParameter( PARAMETER_ID_TABLE_EUDONET_LINK );

            EudonetRestData data = new EudonetRestData( );
            data.setIdConfig( task.getId( ) );
            data.setOrderEntry( Integer.parseInt( ordreEntry ) );
            data.setIdTable( eudonetTable );
            data.setIdAttribut( eudonetAttribut );
            data.setIdTableLink( eudonetTableLink );

            TaskEudonetRestConfigHome.creatEntry( data );

            return null;
        }
        else
            if ( ( request.getParameter( PARAMETER_ID_ENTRY ) != null ) && ( request.getParameter( PARAMETER_CREAT_ENTRY ) != null )
                    && PARAMETER_DELETE_ENTRY.equals( request.getParameter( PARAMETER_CREAT_ENTRY ) )
                    && ( Integer.parseInt( idDirectory ) != WorkflowUtils.CONSTANT_ID_NULL ) )
            {
                String idEntry = request.getParameter( PARAMETER_ID_ENTRY );
                TaskEudonetRestConfigHome.deleteEntry( Integer.parseInt( idEntry ) );

                return null;
            }

        _config.setIdDirectory( Integer.parseInt( idDirectory ) );
        _config.setIdTableEudonet( idTableEudonet );
        _config.setBaseUrl( baseUrl );
        _config.setSubscriberLogin( subscriberLogin );
        _config.setSubscriberPassword( subscriberPassword );
        _config.setBaseName( baseName );
        _config.setUserLogin( userLogin );
        _config.setUserPassword( userPassword );
        _config.setUserLang( userLang );
        _config.setProductName( productName );

        if ( _client == null )
        {
            _client = new EudonetClient( _config.getBaseUrl( ) );
        }

        if ( bCreate )
        {
            _taskEudonetConfigService.create( _config );
        }
        else
        {
            _taskEudonetConfigService.update( _config );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<String, Object>( );
        String strIdTask = request.getParameter( EudonetRestDirctoryConstants.PARAMETER_ID_TASK );
        // TaskEudonetRestConfig taskEudonetConfig = null;

        int nIdDirectory;

        if ( StringUtils.isNotBlank( request.getParameter( EudonetRestDirctoryConstants.PARAMETER_ID_DIRECTORY ) ) )
        {
            nIdDirectory = DirectoryUtils.convertStringToInt( request.getParameter( EudonetRestDirctoryConstants.PARAMETER_ID_DIRECTORY ) );
        }
        else
        {
            nIdDirectory = -1;
        }

        if ( StringUtils.isNotBlank( strIdTask ) )
        {
            _config = _taskEudonetConfigService.findByPrimaryKey( DirectoryUtils.convertStringToInt( strIdTask ) );

            if ( _config != null )
            {
                model.put( MARKER_TASK_EUDONET_CONFIG, _config );

                List<EudonetRestData> entries = (List<EudonetRestData>) _config.getEntry( );

                model.put( MARKER_ENTRY, entries );
                nIdDirectory = _config.getIdDirectory( );
            }
            else
            {
                model.put( MARKER_TASK_EUDONET_CONFIG, new TaskEudonetRestConfig( ) );
            }
        }

        ReferenceList tableList = getEudonetTables( );

        ReferenceList attributList = getEudonetAttribut( );

        model.put( EudonetRestDirctoryConstants.MARK_DIRECTORY_LIST, getListDirectories( ) );
        model.put( EudonetRestDirctoryConstants.MARK_LIST_ENTRIES, getListEntries( nIdDirectory, request ) );
        model.put( MARKER_LIST_TABLE_EUDONET, tableList );
        model.put( MARKER_LIST_TABLE_EUDONET_LINK, tableList );
        model.put( MARKER_LIST_ATTRIBUT_EUDONET, attributList );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_EUDONET, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    /**
     * Get the list of directorise
     * 
     * @return a ReferenceList
     */
    private static ReferenceList getListDirectories( )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        ReferenceList listDirectories = DirectoryHome.getDirectoryList( pluginDirectory );
        ReferenceList refenreceListDirectories = new ReferenceList( );
        refenreceListDirectories.addItem( DirectoryUtils.CONSTANT_ID_NULL, StringUtils.EMPTY );

        if ( listDirectories != null )
        {
            refenreceListDirectories.addAll( listDirectories );
        }

        return refenreceListDirectories;
    }

    /**
     * Method to get eudonet attributs list
     * 
     * @return tables list
     */
    private ReferenceList getEudonetTables( )
    {
        ReferenceList tableList = new ReferenceList( );

        ReferenceList referenceList = new ReferenceList( );
        ReferenceItem referenceItemVide = new ReferenceItem( );
        referenceItemVide.setCode( "" );
        referenceItemVide.setName( "" );
        referenceList.add( referenceItemVide );

        if ( _config != null )
        {
            if ( _client == null )
            {
                _client = new EudonetClient( _config.getBaseUrl( ) );
            }

            String strToken = token( );
            JSONArray tableListJson = getTableListJson( strToken );
            tableList = getTableList( tableListJson );

            for ( ReferenceItem item : tableList )
            {
                ReferenceItem referenceItem = new ReferenceItem( );
                referenceItem.setCode( item.getCode( ) + "-" + item.getName( ) );
                referenceItem.setName( item.getName( ) );
                referenceList.add( referenceItem );
            }
        }

        return referenceList;
    }

    /**
     * Method to get eudonet tables list
     * 
     * @return attributs list
     */
    private ReferenceList getEudonetAttribut( )
    {
        ReferenceList referenceList = new ReferenceList( );

        ReferenceList attributList = new ReferenceList( );
        ReferenceItem referenceItemVide = new ReferenceItem( );
        referenceItemVide.setCode( "" );
        referenceItemVide.setName( "" );
        referenceList.add( referenceItemVide );

        if ( _config != null )
        {
            if ( _client == null )
            {
                _client = new EudonetClient( _config.getBaseUrl( ) );
            }

            String strToken = token( );

            String strIdTableEudonet = _config.getIdTableEudonet( ).split( "-" ) [0];

            JSONArray attributListJson = getAttributListJson( strToken, strIdTableEudonet );

            attributList = getAttributList( attributListJson );

            for ( ReferenceItem item : attributList )
            {
                boolean isContain = false;

                for ( EudonetRestData ent : _config.getEntry( ) )
                {
                    String strIdattEudonet = item.getCode( ) + "-" + item.getName( );
                    if ( strIdattEudonet.equals( ent.getIdAttribut( ) ) )
                    {
                        isContain = true;

                        break;
                    }
                }

                if ( !isContain )
                {
                    ReferenceItem referenceItem = new ReferenceItem( );
                    referenceItem.setCode( item.getCode( ) + "-" + item.getName( ) );
                    referenceItem.setName( item.getName( ) );
                    referenceList.add( referenceItem );
                }
            }
        }

        return referenceList;
    }

    /**
     * Method to get directory entries list
     * 
     * @param nIdDirectory
     *            id directory
     * @param request
     *            request
     * @return ReferenceList entries list
     */
    private static ReferenceList getListEntries( int nIdDirectory, HttpServletRequest request )
    {
        if ( nIdDirectory != -1 )
        {
            Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
            List<IEntry> listEntries = DirectoryUtils.getFormEntries( nIdDirectory, pluginDirectory, AdminUserService.getAdminUser( request ) );
            ReferenceList referenceList = new ReferenceList( );
            ReferenceItem referenceItemVide = new ReferenceItem( );
            referenceItemVide.setCode( "" );
            referenceItemVide.setName( "" );
            referenceList.add( referenceItemVide );

            for ( IEntry entry : listEntries )
            {
                if ( entry.getEntryType( ).getComment( ) )
                {
                    continue;
                }

                if ( entry.getEntryType( ).getGroup( ) )
                {
                    if ( entry.getChildren( ) != null )
                    {
                        for ( IEntry child : entry.getChildren( ) )
                        {
                            if ( child.getEntryType( ).getComment( ) )
                            {
                                continue;
                            }

                            ReferenceItem referenceItem = new ReferenceItem( );
                            referenceItem.setCode( String.valueOf( child.getIdEntry( ) ) );
                            referenceItem.setName( child.getTitle( ) );
                            referenceList.add( referenceItem );
                        }
                    }
                }
                else
                {
                    ReferenceItem referenceItem = new ReferenceItem( );
                    referenceItem.setCode( String.valueOf( entry.getIdEntry( ) ) );
                    referenceItem.setName( entry.getTitle( ) );
                    referenceList.add( referenceItem );
                }
            }

            return referenceList;
        }
        else
        {
            return new ReferenceList( );
        }
    }

    public String getAuthenticateJsonBody( )
    {
        JSONObject jsonObject = new JSONObject( );
        jsonObject.accumulate( "SubscriberLogin", _config.getSubscriberLogin( ) );
        jsonObject.accumulate( "SubscriberPassword", _config.getSubscriberPassword( ) );
        jsonObject.accumulate( "BaseName", _config.getBaseName( ) );
        jsonObject.accumulate( "UserLogin", _config.getUserLogin( ) );
        jsonObject.accumulate( "UserPassword", _config.getUserPassword( ) );
        jsonObject.accumulate( "UserLang", _config.getUserLang( ) );
        jsonObject.accumulate( "ProductName", _config.getProductName( ) );

        return jsonObject.toString( );
    }

    public String getAttributListJsonBody( String strIdTable )
    {
        if ( strIdTable != null )
        {
            // int nIdTable = Integer.parseInt(strIdTable);
            JSONObject jsonObject = new JSONObject( );
            JSONArray jsonArray = new JSONArray( );
            JSONObject jsonElement = new JSONObject( );
            JSONArray jsonElementArray = new JSONArray( );

            jsonElementArray.add( Integer.parseInt( strIdTable ) );

            jsonElement.accumulate( "DescId", Integer.parseInt( strIdTable ) );
            jsonElement.accumulate( "AllFields", true );
            jsonElement.accumulate( "Fields", jsonElementArray );

            jsonArray.add( jsonElement );

            jsonObject.accumulate( "Tables", jsonArray );

            return jsonObject.toString( );
        }

        return null;
    }

    public String token( )
    {
        String strAuthenticateJson = getAuthenticateJsonBody( );
        try
        {
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
            AppLogService.error( "Erreur to get a token", ex );
        }

        return null;

    }

    public JSONArray getTableListJson( String strToken )
    {
        if ( strToken != null )
        {
            try
            {
                ClientResponse response = _client.getTableListMetaInfos( strToken );
                if ( response.getStatus( ) == 200 )
                {
                    String strResponse = response.getEntity( String.class );
                    JSONObject jsonObject = new JSONObject( );
                    jsonObject.accumulate( "object", strResponse );
                    String strStatus = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultInfos" ).getString( "Success" );
                    if ( strStatus.equals( "true" ) )
                    {
                        return jsonObject.getJSONObject( "object" ).getJSONObject( "ResultMetaData" ).getJSONArray( "Tables" );
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
                AppLogService.error( "Erreur to generate the table list of eudonet", ex );
            }
        }

        return null;
    }

    public JSONArray getAttributListJson( String strToken, String strIdTable )
    {
        if ( strToken != null )
        {
            try
            {
                String strBody = getAttributListJsonBody( strIdTable );
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
                            return jsonObject.getJSONObject( "object" ).getJSONObject( "ResultMetaData" ).getJSONArray( "Tables" ).getJSONObject( 0 )
                                    .getJSONArray( "Fields" );
                        }
                        else
                        {
                            String strErrorMessage = jsonObject.getJSONObject( "object" ).getJSONObject( "ResultInfos" ).getString( "ErrorMessage" );
                            AppLogService.error( "Error Eudonet : " + strErrorMessage );
                        }
                    }

                }
            }
            catch( Exception ex )
            {
                AppLogService.error( "Erreur to generate the attribute list of eudonet", ex );
            }
        }

        return null;
    }

    public ReferenceList getTableList( JSONArray jsonArray )
    {
        ReferenceList referenceList = new ReferenceList( );

        if ( jsonArray != null )
        {
            for ( int i = 0; i < jsonArray.size( ); i++ )
            {
                ReferenceItem referenceItem = new ReferenceItem( );
                referenceItem.setCode( jsonArray.getJSONObject( i ).getString( "Descid" ) );
                referenceItem.setName( jsonArray.getJSONObject( i ).getString( "Label" ) );
                referenceList.add( referenceItem );
            }
        }

        return referenceList;
    }

    public ReferenceList getAttributList( JSONArray jsonArray )
    {
        ReferenceList referenceList = new ReferenceList( );

        if ( jsonArray != null )
        {
            for ( int i = 0; i < jsonArray.size( ); i++ )
            {
                ReferenceItem referenceItem = new ReferenceItem( );
                referenceItem.setCode( jsonArray.getJSONObject( i ).getString( "DescId" ) );
                referenceItem.setName( jsonArray.getJSONObject( i ).getString( "Label" ) );
                referenceList.add( referenceItem );
            }
        }

        return referenceList;
    }
}
