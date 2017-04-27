package fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business;

import java.util.List;

import fr.paris.lutece.portal.service.spring.SpringContextService;

public final class EudonetLinkHome
{
    private static EudonetLinkDAO _dao = SpringContextService.getBean( "workflow-eudonetrestdirectory.eudonetLinkDAO" );

    /**
     * create a eudonetLink
     * 
     * @param eudonetLink
     */
    public static void create( EudonetLink eudonetLink )
    {
        _dao.insert( eudonetLink );
    }

    /**
     * update a eudonetLink
     * 
     * @param eudonetLink
     */
    public static void update( EudonetLink eudonetLink )
    {
        _dao.store( eudonetLink );
    }

    /**
     * delete a eudonetLink by nIdEudonetLink
     * 
     * @param nIdEudonetLink
     */
    public static void delete( int nIdEudonetLink )
    {
        _dao.delete( nIdEudonetLink );
    }

    /**
     * 
     * @param nIdEudonetLink
     * @return
     */
    public static EudonetLink find( int nIdEudonetLink )
    {
        return _dao.load( nIdEudonetLink );
    }

    /**
     * 
     * @param nIdRessource
     * @param nIdTable
     * @return
     */
    public static List<EudonetLink> findAll( int nIdRessource, int nIdTable )
    {
        return _dao.loadAll( nIdRessource, nIdTable );
    }

    /**
     * 
     * @param nIdRessource
     * @param nIdTable
     * @return
     */
    public static EudonetLink findBy( int nIdRessource, int nIdTable )
    {
        return _dao.loadBy( nIdRessource, nIdTable );
    }

}
