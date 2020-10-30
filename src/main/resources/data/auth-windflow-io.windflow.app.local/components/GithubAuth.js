import { FlowIcon } from '/modules/coreComponents.js';

const GITHUB_TOKEN_COOKIE = 'token_exchange'

/*@TODO:Get a state (nonce) from the server and compare it later. At the moment it's a MD5 hash of 'windflow.io'*/
export default {
    name: 'GithubAuth',
    components: {
        FlowIcon,
    },
    data() {
        return {
            githubExtensionUrl: 'http://auth.windflow.io.local:8080/api/auth/github',
            githubTokenExchangeUrl: 'http://auth.windflow.io.local:8080/api/auth/github/exchange'
        }
    },
    template: `
        <h1 class="text-4xl text-white mb-5">GitHub Authentication</h1>
        <a
            :href="githubExtensionUrl"
            class="flex border border-white p-1 w-40 rounded-lg text-white bg-blue-700 justify-center hover:bg-blue-600 items-center text-sm"
        >
            <flow-icon
                icon="brands github"
                class="pr-2 w-6 h-6"
            />
            Login with Github
        </a>
        <div class="mt-8 text-gray-500 text-sm">
            <ul>
                <li>Documentation: <a href="https://docs.github.com/en/free-pro-team@latest/developers/apps/authorizing-oauth-apps" class="text-blue-400 hover:text-blue-300">https://docs.github.com/en/free-pro-team@latest/developers/apps/authorizing-oauth-apps</a></li>
                <li>Settings: <a href="https://github.com/settings/applications/1389161" class="text-blue-400 hover:text-blue-300">https://github.com/settings/applications/1389161</a></li>
            </ul>
        </div>
    `,
    mounted() {
        const gitHubToken = this.getCookieValue(GITHUB_TOKEN_COOKIE);

        if (gitHubToken) {
            const exchangeUrl = [this.githubTokenExchangeUrl, gitHubToken].join('/');
            fetch(exchangeUrl)
                .then(r => r.json())
                .then(data => console.log(data));
        }
    },
    methods: {
        getCookieValue(cookieName) {
            try {
                return document.cookie
                    .split('; ')
                    .find(row => row.startsWith(cookieName))
                    .split('=')[1];
            } catch (error) {
                return null;
            }
        }
    }
}
