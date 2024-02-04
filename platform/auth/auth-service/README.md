

unset ACCESS_TOKEN
ACCESS_TOKEN=$(curl -k http://client:secret@localhost:8443/auth/oauth/token -d grant_type=client_credentials -d scope="all" -s | jq -r .access_token)
echo $ACCESS_TOKEN


curl -H "Authorization: Bearer $ACCESS_TOKEN" -k https://localhost:8443/
product-composite/1 -w "%{http_code}\n" -o /dev/null -s