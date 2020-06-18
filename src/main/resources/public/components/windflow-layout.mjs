import SideMenu from '/components/side-menu.mjs';
import MainContent from '/components/main-content.mjs';

export default {
    components: {
        SideMenu, MainContent
    },
    data() {
        return {
            menuArea: 'SideMenu',
            contentArea: 'MainContent'
        }
    }
}
