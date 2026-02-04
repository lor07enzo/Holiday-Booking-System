import { OverviewSection } from "@/components/overview-section";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { useUsers } from "@/context/user-context";


function Homepage() {
    const { users, hosts, habitations, loading } = useUsers();


    const totalReservationsBeds = habitations.length > 0 ? habitations.reduce((sum, h) => sum + h.rooms, 0) / habitations.length : 0;

    const totalHabitations = habitations.length;
    const totalHosts = hosts.length;
    const totalReservations = 1;
    const mediaNumberRooms = Math.round(totalReservationsBeds * 10) / 10;



    if (loading) {
        return <div className="w-full h-screen flex items-center justify-center">Loading...</div>;
    }

    return(
        <div className="w-full h-screen flex flex-col items-center p-4 justify-start ">
            <div className="flex flex-col  ">
                <div className="text-muted-foreground ">
                    <section>
                        <h1 className="font-bold text-xl text-muted-foreground py-4">Overview</h1>
                        <OverviewSection
                            totalHabitations={totalHabitations}
                            totalHosts={totalHosts}
                            totalReservations={totalReservations}
                            mediaNumberRooms={mediaNumberRooms}
                        />
                    </section>
                    
                </div>

               <div>
                    <h1 className="font-bold text-lg text-center py-2">List of All Users</h1>
                    <div className=" p-2 bg-gray-100 border rounded-lg max-w-6xl w-full">
                        <Table className="w-full">
                            <TableHeader>
                                <TableRow className="grid grid-cols-5">
                                    <TableHead className="w-25">ID</TableHead>
                                    <TableHead>Name</TableHead>
                                    <TableHead>Last Name</TableHead>
                                    <TableHead>Email</TableHead>
                                    <TableHead className="text-right">Address</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {users.length === 0 ? (
                                    <TableRow>
                                        <TableCell colSpan={5} className="text-center text-muted-foreground">
                                            No users found
                                        </TableCell>
                                    </TableRow>
                                ) : (
                                    users.map((user) => (
                                        <TableRow key={user.id} className="grid grid-cols-5">
                                            <TableCell className="font-medium">{user.id}</TableCell>
                                            <TableCell>{user.name}</TableCell>
                                            <TableCell>{user.lastName}</TableCell>
                                            <TableCell>{user.email}</TableCell>
                                            <TableCell className="text-right ">{user.address}</TableCell>
                                        </TableRow>
                                    ))
                                )}
                            </TableBody>
                        </Table>
                    </div>
               </div>

                <div>
                    <h1 className="font-bold text-lg text-center py-2">List of All Hosts</h1>
                    <div className=" p-2 bg-gray-100 border rounded-lg max-w-6xl w-full">
                        <Table className="w-full">
                            <TableHeader>
                                <TableRow className="grid grid-cols-5">
                                    <TableHead className="j">ID</TableHead>
                                    <TableHead>Host Code</TableHead>
                                    <TableHead>Name</TableHead>
                                    <TableHead>Last Name</TableHead>
                                    <TableHead className="text-right">Super Host</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {hosts.length === 0 ? (
                                    <TableRow>
                                        <TableCell colSpan={5} className="text-center text-muted-foreground">
                                            No hosts found
                                        </TableCell>
                                    </TableRow>
                                ) : (
                                    hosts.map((host) => (
                                        <TableRow key={host.user.id} className="grid grid-cols-5">
                                            <TableCell className="font-medium">{host.user.id}</TableCell>
                                            <TableCell>{host.hostCode}</TableCell>
                                            <TableCell>{host.user.name}</TableCell>
                                            <TableCell>{host.user.lastName}</TableCell>
                                            <TableCell className="text-right">
                                                {host.superHost ? "Yes" : "No"}
                                            </TableCell>
                                        </TableRow>
                                    ))
                                )}
                            </TableBody>
                        </Table>
                    </div>
                </div>

                
            </div>
        </div>
    )
}
export default Homepage