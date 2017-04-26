package fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business;

public class EudonetLink
{
    private int _nId;
    private int _nIdRessource;
    private String _strIdField;
    private String _strIdTable;
    private String _strIdTableLink;

    /**
     * @return the _nId
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * @param _nId
     *            the _nId to set
     */
    public void setId( int nId )
    {
        this._nId = nId;
    }

    /**
     * @return the _nIdRessource
     */
    public int getIdRessource( )
    {
        return _nIdRessource;
    }

    /**
     * @param _nIdRessource
     *            the _nIdRessource to set
     */
    public void setIdRessource( int nIdRessource )
    {
        this._nIdRessource = nIdRessource;
    }

    /**
     * @return the _strIdField
     */
    public String getIdField( )
    {
        return _strIdField;
    }

    /**
     * @param _strIdTable
     *            the _strIdField to set
     */
    public void setIdField( String strIdField )
    {
        this._strIdField = strIdField;
    }

    /**
     * @return the _strIdTable
     */
    public String getIdTable( )
    {
        return _strIdTable;
    }

    /**
     * @param _strIdTable
     *            the _strIdTable to set
     */
    public void setIdTable( String strIdTable )
    {
        this._strIdTable = strIdTable;
    }

    /**
     * @return the _strIdTableLink
     */
    public String getIdTableLink( )
    {
        return _strIdTableLink;
    }

    /**
     * @param _strIdTableLink
     *            the _strIdTableLink to set
     */
    public void setIdTableLink( String strIdTableLink )
    {
        this._strIdTableLink = strIdTableLink;
    }
}
