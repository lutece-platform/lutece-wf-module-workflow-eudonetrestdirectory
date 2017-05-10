package fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.service.EudonetRestDirectoryPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class EudonetLinkDAO implements IEudonetLinkDAO
{
    private static final String SQL_QUERY_SELECT_ALL_BY_RESOURCE_TABLE = "SELECT id, id_ressource, id_field, id_table, id_table_link FROM task_eudonetrest_table_link WHERE id_ressource = ? AND id_table = ? ;";
    private static final String SQL_QUERY_SELECT_ALL = "SELECT id, id_ressource, id_field, id_table, id_table_link FROM task_eudonetrest_table_link ;";
    private static final String SQL_QUERY_SELECT_ALL_BY_RESOURCE = "SELECT id, id_ressource, id_field, id_table, id_table_link FROM task_eudonetrest_table_link WHERE id_ressource = ? ;";
    private static final String SQL_QUERY_SELECT_BY = "SELECT id, id_ressource, id_field, id_table, id_table_link FROM task_eudonetrest_table_link WHERE id_ressource = ? AND id_table = ? ;";
    private static final String SQL_QUERY_SELECT = "SELECT id, id_ressource, id_field, id_table, id_table_link FROM task_eudonetrest_table_link WHERE id = ? ;";
    private static final String SQL_QUERY_INSERT = "INSERT INTO task_eudonetrest_table_link ( id, id_ressource, id_field, id_table, id_table_link ) VALUES ( ?, ?, ?, ?, ? );";
    private static final String SQL_QUERY_DELETE = "DELETE FROM task_eudonetrest_table_link WHERE id = ? ;";
    private static final String SQL_QUERY_UPDATE = "UPDATE task_eudonetrest_table_link SET id_ressource=?, id_field=?, id_table=?, id_table_link=? WHERE id = ? ;";
    private static final String SQL_QUERY_NEW_PK_PARAM = "SELECT max( id ) FROM task_eudonetrest_table_link";

    /**
     * Generates a new primary key
     * 
     * @param plugin
     *            The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK_PARAM, plugin );
        daoUtil.executeQuery( );

        int nKey = 1;

        if ( daoUtil.next( ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free( );

        return nKey;
    }

    @Override
    public void insert( EudonetLink eudonetLink )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, EudonetRestDirectoryPlugin.getPlugin( ) );

        daoUtil.setInt( 1, newPrimaryKey( EudonetRestDirectoryPlugin.getPlugin( ) ) );
        daoUtil.setInt( 2, eudonetLink.getIdRessource( ) );
        daoUtil.setString( 3, eudonetLink.getIdField( ) );
        daoUtil.setString( 4, eudonetLink.getIdTable( ) );
        daoUtil.setString( 5, eudonetLink.getIdTableLink( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );

    }

    @Override
    public EudonetLink load( int nIdEudonetLink )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, EudonetRestDirectoryPlugin.getPlugin( ) );
        daoUtil.setInt( 1, nIdEudonetLink );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            EudonetLink eudonetLink = new EudonetLink( );
            eudonetLink.setId( daoUtil.getInt( 1 ) );
            eudonetLink.setIdRessource( daoUtil.getInt( 2 ) );
            eudonetLink.setIdField( daoUtil.getString( 3 ) );
            eudonetLink.setIdTable( daoUtil.getString( 4 ) );
            eudonetLink.setIdTableLink( daoUtil.getString( 5 ) );

            daoUtil.free( );

            return eudonetLink;
        }
        else
        {
            daoUtil.free( );

            return null;
        }
    }

    @Override
    public void store( EudonetLink eudonetLink )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, EudonetRestDirectoryPlugin.getPlugin( ) );
        daoUtil.setInt( 1, eudonetLink.getIdRessource( ) );
        daoUtil.setString( 2, eudonetLink.getIdField( ) );
        daoUtil.setString( 3, eudonetLink.getIdTable( ) );
        daoUtil.setString( 4, eudonetLink.getIdTableLink( ) );

        daoUtil.setInt( 5, eudonetLink.getId( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    @Override
    public void delete( int nKey )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, EudonetRestDirectoryPlugin.getPlugin( ) );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    @Override
    public List<EudonetLink> loadAll( int nIdRessource, int nIdTable )
    {
        List<EudonetLink> eudonetLinkList = new ArrayList<EudonetLink>( );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL_BY_RESOURCE_TABLE, EudonetRestDirectoryPlugin.getPlugin( ) );
        daoUtil.setInt( 1, nIdRessource );
        daoUtil.setInt( 2, nIdTable );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            EudonetLink eudonetLink = new EudonetLink( );
            eudonetLink.setId( daoUtil.getInt( 1 ) );
            eudonetLink.setIdRessource( daoUtil.getInt( 2 ) );
            eudonetLink.setIdField( daoUtil.getString( 3 ) );
            eudonetLink.setIdTable( daoUtil.getString( 4 ) );
            eudonetLink.setIdTableLink( daoUtil.getString( 5 ) );

            eudonetLinkList.add( eudonetLink );
        }

        daoUtil.free( );

        return eudonetLinkList;
    }

    @Override
    public List<EudonetLink> loadAll( int nIdRessource )
    {
        List<EudonetLink> eudonetLinkList = new ArrayList<EudonetLink>( );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL_BY_RESOURCE, EudonetRestDirectoryPlugin.getPlugin( ) );
        daoUtil.setInt( 1, nIdRessource );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            EudonetLink eudonetLink = new EudonetLink( );
            eudonetLink.setId( daoUtil.getInt( 1 ) );
            eudonetLink.setIdRessource( daoUtil.getInt( 2 ) );
            eudonetLink.setIdField( daoUtil.getString( 3 ) );
            eudonetLink.setIdTable( daoUtil.getString( 4 ) );
            eudonetLink.setIdTableLink( daoUtil.getString( 5 ) );

            eudonetLinkList.add( eudonetLink );
        }

        daoUtil.free( );

        return eudonetLinkList;
    }

    @Override
    public List<EudonetLink> loadAll( )
    {
        List<EudonetLink> eudonetLinkList = new ArrayList<EudonetLink>( );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, EudonetRestDirectoryPlugin.getPlugin( ) );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            EudonetLink eudonetLink = new EudonetLink( );
            eudonetLink.setId( daoUtil.getInt( 1 ) );
            eudonetLink.setIdRessource( daoUtil.getInt( 2 ) );
            eudonetLink.setIdField( daoUtil.getString( 3 ) );
            eudonetLink.setIdTable( daoUtil.getString( 4 ) );
            eudonetLink.setIdTableLink( daoUtil.getString( 5 ) );

            eudonetLinkList.add( eudonetLink );
        }

        daoUtil.free( );

        return eudonetLinkList;
    }

    @Override
    public EudonetLink loadBy( int nIdRessource, int nIdTable )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY, EudonetRestDirectoryPlugin.getPlugin( ) );
        daoUtil.setInt( 1, nIdRessource );
        daoUtil.setInt( 2, nIdTable );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            EudonetLink eudonetLink = new EudonetLink( );
            eudonetLink.setId( daoUtil.getInt( 1 ) );
            eudonetLink.setIdRessource( daoUtil.getInt( 2 ) );
            eudonetLink.setIdField( daoUtil.getString( 3 ) );
            eudonetLink.setIdTable( daoUtil.getString( 4 ) );
            eudonetLink.setIdTableLink( daoUtil.getString( 5 ) );

            daoUtil.free( );

            return eudonetLink;
        }
        else
        {
            daoUtil.free( );
            return null;
        }
    }
}
