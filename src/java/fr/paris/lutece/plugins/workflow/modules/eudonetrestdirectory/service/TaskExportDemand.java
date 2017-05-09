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
package fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.service;

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.gfa.business.GfaResourceDTO;
import fr.paris.lutece.plugins.gfa.business.service.IProcessTaskErrorService;
import fr.paris.lutece.plugins.gfa.business.service.ProcessTaskErrorService;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business.EudonetRestData;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business.TaskEudonetRestConfig;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business.TaskEudonetRestConfigHome;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * TaskExportDemand
 *
 */
public class TaskExportDemand extends SimpleTask
{
    // <constants
    private static final String CONSTANT_RESOURCE_ID_APPOINTMENTFORM = "1";
    private static final String CONSTANT_RESOURCE_ID_DIRECTORY = "2";

    // PROPERTIES
    private static final String PROPERTY_TASK_TITLE = "workflow.eudonetrestdirectory.task.title";
    public static final String CONFIG_SERVICE_BEAN_NAME = "workflow-eudonetrestdirectory.taskEudonetRestConfigService";
    private static final String ACTION = "ACDP ACTION";
    private static IEudonetRestWsService _eudonetService;

    // SERVICES

    // SERVICES
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    @Named( CONFIG_SERVICE_BEAN_NAME )
    private ITaskConfigService _taskConfigService;

    IProcessTaskErrorService _erroService = ProcessTaskErrorService.getService( );

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale locale )
    {
        return AppPropertiesService.getProperty( PROPERTY_TASK_TITLE );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        TaskEudonetRestConfig taskEudonetConfig = _taskConfigService.findByPrimaryKey( getId( ) );

        if ( ( resourceHistory != null ) && ( taskEudonetConfig != null ) )
        {
            List<EudonetRestData> eudonetCollection = TaskEudonetRestConfigHome.selectEntry( taskEudonetConfig.getIdTask( ) );
            taskEudonetConfig.setEntry( eudonetCollection );

            try
            {
                _eudonetService = EudonetRestWsEudonetRest.getInstance( );
                _eudonetService.init( taskEudonetConfig );
                _eudonetService.exportDemand( resourceHistory.getIdResource( ), resourceHistory.getAction( ).getId( ) );
            }
            catch( Exception e )
            {
                AppLogService.error( "Error export demand to eudonet process task", e );
                throw new RuntimeException( e );
            }
        }
    }
}
