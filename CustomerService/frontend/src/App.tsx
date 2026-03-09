import { useEffect, useState } from "react";

type Customer = {
  id: number;
  name: string;
  email: string;
  mobile?: string;
};

type PagedResponse<T> = {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
};

type AuthResponse = {
  token: string;
  username: string;
};

function App() {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [token, setToken] = useState<string | null>(
    () => localStorage.getItem("token")
  );

  // Pagination state
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(5);
  const [sortBy, setSortBy] = useState("id");
  const [totalPages, setTotalPages] = useState(0);

  // Forms for CRUD/search
  const [searchId, setSearchId] = useState("");
  const [searchName, setSearchName] = useState("");
  const [searchEmail, setSearchEmail] = useState("");

  const [editId, setEditId] = useState("");
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [mobile, setMobile] = useState("");
  const [deleteId, setDeleteId] = useState("");

  const isLoggedIn = Boolean(token);

  const authorizedFetch = async (
    input: RequestInfo | URL,
    init: RequestInit = {}
  ) => {
    if (!token) {
      throw new Error("Not authenticated");
    }
    const headers = new Headers(init.headers || {});
    if (!headers.has("Authorization")) {
      headers.set("Authorization", `Bearer ${token}`);
    }
    return fetch(input, { ...init, headers });
  };

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setError(null);
      setLoading(true);

      const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ username, password })
      });

      if (!res.ok) {
        throw new Error("Login failed");
      }

      const data: AuthResponse = await res.json();
      localStorage.setItem("token", data.token);
      setToken(data.token);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Login error");
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    setToken(null);
    setCustomers([]);
  };

  const loadPage = async (newPage = page) => {
    try {
      setLoading(true);
      setError(null);
      const response = await authorizedFetch(
        `/api/customer/AllCustomersDetails?page=${newPage}&size=${size}&sortBy=${sortBy}`
      );
      if (!response.ok) {
        throw new Error("Failed to load customers");
      }
      const data: PagedResponse<Customer> = await response.json();
      setCustomers(data.content ?? []);
      setPage(data.pageNumber);
      setTotalPages(data.totalPages);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unknown error");
    } finally {
      setLoading(false);
    }
  };

  const handleSearchById = async () => {
    if (!searchId) {
      return;
    }
    try {
      setLoading(true);
      setError(null);
      const res = await authorizedFetch(`/api/customer/customerId/${searchId}`);
      if (!res.ok) {
        throw new Error("Customer not found");
      }
      const data: Customer = await res.json();
      setCustomers([data]);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Search error");
    } finally {
      setLoading(false);
    }
  };

  const handleSearchByName = async () => {
    if (!searchName) {
      return;
    }
    try {
      setLoading(true);
      setError(null);
      const res = await authorizedFetch(
        `/api/customer/customerName/${encodeURIComponent(searchName)}`
      );
      if (!res.ok) {
        throw new Error("Customer not found");
      }
      const data: Customer = await res.json();
      setCustomers([data]);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Search error");
    } finally {
      setLoading(false);
    }
  };

  const handleSearchByEmail = async () => {
    if (!searchEmail) {
      return;
    }
    try {
      setLoading(true);
      setError(null);
      const res = await authorizedFetch(
        `/api/customer/customerEmail/${encodeURIComponent(searchEmail)}`
      );
      if (!res.ok) {
        throw new Error("Customer not found");
      }
      const data: Customer = await res.json();
      setCustomers([data]);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Search error");
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await authorizedFetch("/api/customer", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ name, email, mobile })
      });
      if (!res.ok) {
        throw new Error("Create failed");
      }
      await loadPage(0);
      setEditId("");
      setName("");
      setEmail("");
      setMobile("");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Create error");
    } finally {
      setLoading(false);
    }
  };

  const handleUpdate = async () => {
    if (!editId) {
      setError("ID is required for update");
      return;
    }
    try {
      setLoading(true);
      setError(null);
      const res = await authorizedFetch(
        `/api/customer/updateCustomer/${editId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({ name, email, mobile })
        }
      );
      if (!res.ok) {
        throw new Error("Update failed");
      }
      await loadPage(page);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Update error");
    } finally {
      setLoading(false);
    }
  };

  const handlePatch = async () => {
    if (!editId) {
      setError("ID is required for patch");
      return;
    }
    try {
      setLoading(true);
      setError(null);
      const body: Record<string, string> = {};
      if (name) body.name = name;
      if (email) body.email = email;
      if (mobile) body.mobile = mobile;

      const res = await authorizedFetch(`/api/customer/${editId}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
      });
      if (!res.ok) {
        throw new Error("Patch failed");
      }
      await loadPage(page);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Patch error");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteId) {
      setError("ID is required for delete");
      return;
    }
    try {
      setLoading(true);
      setError(null);
      const res = await authorizedFetch(`/api/customer/deleteCustomer/${deleteId}`, {
        method: "DELETE"
      });
      if (!res.ok) {
        throw new Error("Delete failed");
      }
      await loadPage(page);
      setDeleteId("");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Delete error");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (token) {
      loadPage(0).catch(() => undefined);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  return (
    <div className="app">
      <header className="app-header">
        <h1>Customer Service</h1>
      </header>

      <main className="card">
        {!isLoggedIn ? (
          <form onSubmit={handleLogin} className="login-form">
            <h2>Sign in</h2>
            <label>
              Username
              <input
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </label>
            <label>
              Password
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </label>
            <button type="submit" disabled={loading}>
              {loading ? "Signing in..." : "Sign in"}
            </button>
            {error && <p className="error">{error}</p>}
          </form>
        ) : (
          <>
            <div className="toolbar">
              <span>
                Logged in as <strong>{username || "admin"}</strong>
              </span>
              <button type="button" onClick={handleLogout}>
                Logout
              </button>
            </div>

            {error && <p className="error">{error}</p>}

            <section className="section">
              <h2>Search customers</h2>
              <div className="grid">
                <div className="field-row">
                  <label>
                    By ID
                    <input
                      value={searchId}
                      onChange={(e) => setSearchId(e.target.value)}
                    />
                  </label>
                  <button type="button" onClick={handleSearchById}>
                    Search
                  </button>
                </div>
                <div className="field-row">
                  <label>
                    By name
                    <input
                      value={searchName}
                      onChange={(e) => setSearchName(e.target.value)}
                    />
                  </label>
                  <button type="button" onClick={handleSearchByName}>
                    Search
                  </button>
                </div>
                <div className="field-row">
                  <label>
                    By email
                    <input
                      value={searchEmail}
                      onChange={(e) => setSearchEmail(e.target.value)}
                    />
                  </label>
                  <button type="button" onClick={handleSearchByEmail}>
                    Search
                  </button>
                </div>
              </div>
            </section>

            <section className="section">
              <h2>Customers list</h2>
              <div className="controls">
                <label>
                  Page size
                  <input
                    type="number"
                    min={1}
                    value={size}
                    onChange={(e) => setSize(Number(e.target.value) || 1)}
                  />
                </label>
                <label>
                  Sort by
                  <select
                    value={sortBy}
                    onChange={(e) => setSortBy(e.target.value)}
                  >
                    <option value="id">ID</option>
                    <option value="name">Name</option>
                    <option value="email">Email</option>
                  </select>
                </label>
                <button type="button" onClick={() => loadPage(0)}>
                  Refresh
                </button>
              </div>

              {loading && <p>Loading customers...</p>}

              {!loading && (
                <>
                  {customers.length === 0 ? (
                    <p>No customers found.</p>
                  ) : (
                    <table>
                      <thead>
                        <tr>
                          <th>ID</th>
                          <th>Name</th>
                          <th>Email</th>
                          <th>Mobile</th>
                        </tr>
                      </thead>
                      <tbody>
                        {customers.map((customer) => (
                          <tr key={customer.id}>
                            <td>{customer.id}</td>
                            <td>{customer.name}</td>
                            <td>{customer.email}</td>
                            <td>{customer.mobile ?? "-"}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  )}

                  <div className="pagination">
                    <button
                      type="button"
                      disabled={page === 0}
                      onClick={() => loadPage(page - 1)}
                    >
                      Previous
                    </button>
                    <span>
                      Page {page + 1} of {Math.max(totalPages, 1)}
                    </span>
                    <button
                      type="button"
                      disabled={totalPages !== 0 && page >= totalPages - 1}
                      onClick={() => loadPage(page + 1)}
                    >
                      Next
                    </button>
                  </div>
                </>
              )}
            </section>

            <section className="section">
              <h2>Create / Update / Patch customer</h2>
              <div className="grid">
                <label>
                  ID (for update/patch)
                  <input
                    value={editId}
                    onChange={(e) => setEditId(e.target.value)}
                  />
                </label>
                <label>
                  Name
                  <input
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                  />
                </label>
                <label>
                  Email
                  <input
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                  />
                </label>
                <label>
                  Mobile
                  <input
                    value={mobile}
                    onChange={(e) => setMobile(e.target.value)}
                  />
                </label>
              </div>
              <div className="controls">
                <button type="button" onClick={handleCreate}>
                  Create
                </button>
                <button type="button" onClick={handleUpdate}>
                  Update (PUT)
                </button>
                <button type="button" onClick={handlePatch}>
                  Patch (partial)
                </button>
              </div>
            </section>

            <section className="section">
              <h2>Delete customer</h2>
              <div className="field-row">
                <label>
                  ID
                  <input
                    value={deleteId}
                    onChange={(e) => setDeleteId(e.target.value)}
                  />
                </label>
                <button type="button" onClick={handleDelete}>
                  Delete
                </button>
              </div>
            </section>
          </>
        )}
      </main>
    </div>
  );
}

export default App;
