package fr.paris.lutece.plugins.workflow.modules.eudonetrestdirectory.business;

import fr.paris.lutece.util.ReferenceItem;

public class ReferenceItemSorted extends ReferenceItem implements Comparable
{
    @Override
    public int compareTo( Object o )
    {
        if ( o.getClass( ).equals( ReferenceItemSorted.class ) )
        {
            ReferenceItemSorted item = (ReferenceItemSorted) o;
            return this.getName( ).compareTo( item.getName( ) );
        }

        return -1;
    }
}
