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

import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business.TaskEudonetRestConfig;
import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.utils.EudonetRestException;

import java.util.Set;

/**
 *
 * this interface provides service for calling web service eudonet
 *
 */
public interface IEudonetRestWsService
{
    /**
     * Methode init service
     * 
     * @param taskEudonetConfig
     */
    void init( TaskEudonetRestConfig taskEudonetConfig ) throws EudonetRestException;

    /**
     * Methode to export Demands to eudonet
     * 
     * @param idResource
     * @throws EudonetRestException
     */
    public void exportDemand( int idResource, int idaction ) throws EudonetRestException;

    /**
     * Update Demands In eudonet
     * 
     * @param idResource
     * @throws EudonetRestException
     */
    public void updateDemand( int idResource ) throws EudonetRestException;

    /**
     * Remove Demands in eudonet
     * 
     * @param idResource
     * @throws EudonetRestException
     */
    public void removeDemand( int idResource ) throws EudonetRestException;

    /**
     * get Teleservice eudonet list
     * 
     * @return attributs list
     */
    public Set<String> getTeleserviceEudonet( );

    /**
     * get Attributs eudonet list by nIdTeleservice
     * 
     * @param nIdTeleservice
     * @return attributs list
     */
    public Set<String> getAttributEudonet( int nIdTeleservice );
}
