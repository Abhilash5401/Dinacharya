/**
 * Same-origin proxy so POST /api/v1/* hits Render instead of static assets (405).
 * Cloudflare Worker env: API_ORIGIN = https://YOUR-SERVICE.onrender.com
 */
export default {
  async fetch(request, env) {
    const url = new URL(request.url);

    if (url.pathname === '/api' || url.pathname.startsWith('/api/')) {
      const origin = String(env.API_ORIGIN || 'https://dinacharya-ese5.onrender.com').replace(
        /\/$/,
        ''
      );

      const target = origin + url.pathname + url.search;
      const headers = new Headers(request.headers);
      headers.delete('host');

      return fetch(target, {
        method: request.method,
        headers,
        body: request.body,
        redirect: 'follow',
        duplex: 'half',
      });
    }

    return env.ASSETS.fetch(request);
  },
};
