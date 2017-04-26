/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business;

import javax.validation.constraints.NotNull;

public class EudonetRestData
{
    private int _nId;
    @NotNull
    private String _strIdAttribut;
    @NotNull
    private String _strIdTable;
    @NotNull
    private String _strIdTableLink;
    @NotNull
    private int _nOrderEntry;
    @NotNull
    private int _nIdConfig;

    /**
     * @return Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * @param id
     */
    public void setId( int id )
    {
        _nId = id;
    }

    /**
     * @return _nIdAttribut
     */
    public String getIdAttribut( )
    {
        return _strIdAttribut;
    }

    /**
     * @param nIdAttribut
     */
    public void setIdAttribut( String strIdAttribut )
    {
        _strIdAttribut = strIdAttribut;
    }

    /**
     * @return EudonetTable
     */
    public String getIdTable( )
    {
        return _strIdTable;
    }

    /**
     * @param _nIdTable
     */
    public void setIdTable( String strIdTable )
    {
        _strIdTable = strIdTable;
    }

    /**
     * @return EudonetTableLink
     */
    public String getIdTableLink( )
    {
        return _strIdTableLink;
    }

    /**
     * @param _nIdTableLink
     */
    public void setIdTableLink( String strIdTableLink )
    {
        _strIdTableLink = strIdTableLink;
    }

    /**
     * @return OrderEntry
     */
    public int getOrderEntry( )
    {
        return _nOrderEntry;
    }

    /**
     * @param orderEntry
     */
    public void setOrderEntry( int orderEntry )
    {
        _nOrderEntry = orderEntry;
    }

    /**
     * @return idConfig
     */
    public int getIdConfig( )
    {
        return _nIdConfig;
    }

    /**
     * @param idConfig
     */
    public void setIdConfig( int idConfig )
    {
        _nIdConfig = idConfig;
    }
}
