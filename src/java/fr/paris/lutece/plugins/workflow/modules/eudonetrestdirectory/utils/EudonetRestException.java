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
package fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.utils;

/**
 *
 * EudonetException
 *
 */
public class EudonetRestException extends Exception
{
    private static final long serialVersionUID = 6610609149888544158L;
    private String _strIdDemand;
    private String _strErrorMessage;

    /**
     * Creates a new EudonetException
     * 
     * @param IdDemand
     *            The Number of the demand that caused the error
     * @param strErrorMessage
     *            The error message
     */
    public EudonetRestException( String strIdDemand, String strErrorMessage )
    {
        _strIdDemand = strIdDemand;
        _strErrorMessage = strErrorMessage;
    }

    /**
     * Creates a new EudonetException
     * 
     * @param _strIdDemand
     *            The Number of the demand that caused the error
     */
    public EudonetRestException( String strIdDemand )
    {
        _strIdDemand = strIdDemand;
    }

    /**
     * Gets the error Message
     * 
     * @return the error Message
     */
    public String getErrorMessage( )
    {
        return _strErrorMessage;
    }

    /**
     * set the error message
     * 
     * @param errorMessage
     *            the erroer message
     */
    public void setErrorMessage( String errorMessage )
    {
        _strErrorMessage = errorMessage;
    }

    /**
     *
     * @return the number of the demand
     */
    public String getIdDemand( )
    {
        return _strIdDemand;
    }

    /**
     * set the IdDemand of the demand
     * 
     * @param IdDemand
     *            the id of the demande
     */
    public void setIdDemand( String strIdDemand )
    {
        _strIdDemand = strIdDemand;
    }
}
