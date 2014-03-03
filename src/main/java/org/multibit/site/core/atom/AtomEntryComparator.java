package org.multibit.site.core.atom;

import java.util.Comparator;

/**
 * <p>Comparator to provide the following to the Atom feed:</p>
 * <ul>
 * <li>Sorting by updated field</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class AtomEntryComparator implements Comparator<AtomEntry> {


  @Override
  public int compare(AtomEntry atomEntry, AtomEntry atomEntry2) {

    if (atomEntry != null && atomEntry2 == null) {
      return 1;
    }

    return atomEntry != null ? atomEntry.getUpdated().compareTo(atomEntry2.getUpdated()) : 0;
  }
}
