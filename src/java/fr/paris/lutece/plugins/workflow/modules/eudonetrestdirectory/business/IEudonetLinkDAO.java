package fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business;

import java.util.List;

public interface IEudonetLinkDAO
{
    /**
     * Insert a new record in the table.
     * 
     * @param eudonetLink
     *            instance of the EudonetLink object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( EudonetLink eudonetLink );

    /**
     * load a record in the table.
     * 
     * @param nIdEudonetLink
     * @param plugin
     *            the Plugin
     */
    EudonetLink load( int nIdEudonetLink );

    /**
     * load a record in the table.
     * 
     * @param nIdEudonetLink
     * @param plugin
     *            the Plugin
     */
    EudonetLink loadBy( int nIdRessource, int nIdTable );

    /**
     * load a record in the table.
     * 
     * @param nIdEudonetLink
     * @param plugin
     *            the Plugin
     */
    List<EudonetLink> loadAll( int nIdRessource, int nIdTable );

    /**
     * 
     * @param nIdRessource
     * @return
     */
    List<EudonetLink> loadAll( int nIdRessource );

    /**
     * 
     * @return
     */
    List<EudonetLink> loadAll( );

    /**
     * Update the record in the table
     * 
     * @param eudonetLink
     *            the reference of the EudonetLink
     * @param plugin
     *            the Plugin
     */
    void store( EudonetLink eudonetLink );

    /**
     * Delete a record from the table
     * 
     * @param nKey
     *            The identifier of the EudonetLink to delete
     * @param plugin
     *            the Plugin
     */
    void delete( int nKey );
}
