declare const VERSION: string;
declare const SERVER_API_URL: string;
declare const DEVELOPMENT: string;
declare const I18N_HASH: string;
declare const CUSTOM_ENV: CustomEnv;

declare module '*.json' {
  const value: any;
  export default value;
}

interface CustomEnv {
  PAYPAL_CLIENT_ID: string;
}
