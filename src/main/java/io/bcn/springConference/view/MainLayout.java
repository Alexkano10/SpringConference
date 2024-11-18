package io.bcn.springConference.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.router.HighlightConditions;

@Route("")
@PageTitle("Spring Conference")
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    // Crea el encabezado con el título de la aplicación
    private void createHeader() {
        H1 logo = new H1("Spring Conference");
        logo.addClassNames("text-l", "m-m");

        // Barra superior con el botón de menú y el logo
        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle(),
                logo
        );
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");

        addToNavbar(header); // Añadir el encabezado a la barra de navegación
    }

    // Crea el menú lateral con los enlaces de las vistas
    private void createDrawer() {
        RouterLink conferenceLink = new RouterLink("Conferences", ConferenceView.class);
        RouterLink speakerLink = new RouterLink("Speakers", SpeakerView.class);
        RouterLink bookLink = new RouterLink("Books", BookView.class);

        // Resaltar la vista activa (cuando la ubicación coincide)
        conferenceLink.setHighlightCondition(HighlightConditions.sameLocation());
        speakerLink.setHighlightCondition(HighlightConditions.sameLocation());
        bookLink.setHighlightCondition(HighlightConditions.sameLocation());

        // Añadir los enlaces al menú lateral
        addToDrawer(new VerticalLayout(
                conferenceLink,
                speakerLink,
                bookLink
        ));
    }
}