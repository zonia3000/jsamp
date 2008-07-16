package org.astrogrid.samp.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.astrogrid.samp.Client;

/**
 * Component displaying a list of SAMP {@link org.astrogrid.samp.Client}s,
 * usually those registered with a hub.
 *
 * @author   Mark Taylor
 * @since    16 Jul 2008
 */
public class HubView extends JPanel {

    private final JList jlist_;
    private final ClientPanel clientPanel_;
    private final ListDataListener listListener_;

    /**
     * Constructor.
     */
    public HubView() {
        super( new BorderLayout() );

        // Set up a JList to display the list of clients.
        // If a selection is made, update the client detail panel.
        jlist_ = new JList();
        ListSelectionModel selModel = jlist_.getSelectionModel();
        selModel.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        selModel.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent evt ) {
                if ( ! evt.getValueIsAdjusting() ) {
                    updateClientView();
                }
            }
        } );

        // Watch the list; if any change occurs which may affect the currently-
        // selected client, update the client detail panel.
        listListener_ = new ListDataListener() {
            public void contentsChanged( ListDataEvent evt ) {
                preferSelection();
                int isel = jlist_.getSelectedIndex();
                int i0 = evt.getIndex0();
                int i1 = evt.getIndex1();
                if ( isel >= 0 && ( i0 < 0 || i1 < 0 ||
                                    ( i0 - isel ) * ( i1 - isel ) <= 0 ) ) {
                    updateClientView();
                }
            }
            public void intervalRemoved( ListDataEvent evt ) {
                if ( clientPanel_.getClient() != null &&
                     jlist_.getSelectedIndex() < 0 ) {
                    updateClientView();
                }
            }
            public void intervalAdded( ListDataEvent evt ) {
                preferSelection();
            }
            private void preferSelection() {
                if ( jlist_.getSelectedIndex() < 0 &&
                     jlist_.getModel().getSize() > 0 ) {
                    jlist_.setSelectedIndex( 0 );
                }
            }
        };

        // Construct and place subcomponents.
        clientPanel_ = new ClientPanel();
        JSplitPane splitter = new JSplitPane();
        splitter.setOneTouchExpandable( true );
        JScrollPane listScroller = new JScrollPane( jlist_ );
        listScroller.setPreferredSize( new Dimension( 120, 500 ) );
        listScroller.setBorder( ClientPanel.createTitledBorder( "Clients" ) );
        splitter.setLeftComponent( listScroller );
        splitter.setRightComponent( clientPanel_ );
        add( splitter );
    }

    /**
     * Sets the client list model which is displayed in this component.
     *
     * @param  clientModel   list model whose elements are 
     *                       {@link org.astrogrid.samp.Client}s
     */
    public void setClientListModel( ListModel clientModel ) {
        ListModel oldModel = jlist_.getModel();
        jlist_.getSelectionModel().clearSelection();
        if ( oldModel != null ) {
            oldModel.removeListDataListener( listListener_ );
        }
        jlist_.setModel( clientModel );
        if ( clientModel != null ) {
            clientModel.addListDataListener( listListener_ );
            jlist_.setCellRenderer( new ClientListCellRenderer( clientModel,
                                                                null ) );
        }
    }

    /**
     * Ensure that the client panel is up to date with respect to the currently
     * selected client.
     */
    private void updateClientView() {
        int isel = jlist_.getSelectedIndex();
        clientPanel_.setClient( isel >= 0
                              ? (Client) jlist_.getModel().getElementAt( isel )
                              : null );
    }
}
