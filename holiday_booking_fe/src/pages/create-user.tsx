import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Field, FieldGroup, FieldLabel } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { useState } from "react";
import { z } from "zod";

const API_URL = import.meta.env.VITE_API_URL;

const createUserFormSchema = z.object({
    name: z.string().min(2, "Name is required"),
    lastName: z.string().min(2, "Last name is required"),
    email: z.string().email("Invalid email"),
    address: z.string().min(5, "Address is required"),
    isHost: z.boolean()
})

export type CreateUserFormData = z.infer<typeof createUserFormSchema>;

export function CreateUser() {
    const [isHost, setIsHost] = useState(false);
    const [errors, setErrors] = useState<Record<string, string>>({});

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const formData = new FormData(e.currentTarget);

        const data = {
            name: formData.get("name"),
            lastName: formData.get("lastName"),
            email: formData.get("email"),
            address: formData.get("address"),
            isHost
        };

        const parsed = createUserFormSchema.safeParse(data);

        if (!parsed.success) {
            const fieldErrors: Record<string, string> = {};
            parsed.error.errors.forEach(err => {
              fieldErrors[err.path[0]] = err.message;
            });
            setErrors(fieldErrors);
            return;
        }

        setErrors({});

        const payload = {
            user: {
                name: parsed.data.name,
                lastName: parsed.data.lastName,
                email: parsed.data.email,
                address: parsed.data.address
            },
            isHost: parsed.data.isHost // backend decide se creare Host
        };

        await fetch(`${API_URL}/api/v1/users`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
    }

    return (
        <div className=" w-full flex flex-col h-screen justify-center items-center">
            <Card className="w-full max-w-4xl">
                <CardHeader>
                    <CardTitle>Create a new User</CardTitle>
                    <CardDescription>
                        Enter your information below to create your account
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit}>
                        <FieldGroup >
                            <div className="grid grid-cols-2 gap-4">
                                <Field>
                                    <FieldLabel htmlFor="name">Name</FieldLabel>
                                    <Input id="name" name="name" placeholder="John" aria-invalid={!!errors.name}  />
                                    {errors.name && <p className="text-red-500 text-sm">{errors.name}</p>}
                                </Field>

                                <Field>
                                    <FieldLabel htmlFor="lastName">Last Name</FieldLabel>
                                    <Input id="lastName" name="lastName" placeholder="Doe" aria-invalid={!!errors.lastName}  />
                                    {errors.lastName && <p className="text-red-500 text-sm">{errors.lastName}</p>}
                                </Field>

                                <Field>
                                    <FieldLabel htmlFor="email">Email</FieldLabel>
                                    <Input id="email" name="email" type="email" placeholder="m@example.com" aria-invalid={!!errors.email} />  
                                    {errors.email && <p className="text-red-500 text-sm">{errors.email}</p>}
                                </Field>
                                {/*TODO: Modificare i required ed unire i tipi di indirizzi */}
                                <Field>
                                    <FieldLabel htmlFor="address">Residential Address</FieldLabel>
                                    <Input id="address" type="text" placeholder="Via Roma, 56" required />
                                </Field>

                                <Field>
                                    <FieldLabel htmlFor="city">City</FieldLabel>
                                    <Input id="city" type="text" placeholder="Firenze" required />
                                </Field>

                                <Field>
                                    <FieldLabel htmlFor="country">Country</FieldLabel>
                                    <Input id="country" type="text" placeholder="Italy" required />
                                </Field>
                            </div>

                            <Field>
                                <FieldLabel htmlFor="isHost">User type</FieldLabel>
                                <div className="flex items-center gap-2">
                                    <input
                                        id="isHost"
                                        type="checkbox"
                                        checked={isHost}
                                        onChange={(e) => setIsHost(e.target.checked)}
                                        className="h-4 w-4"
                                    />
                                    <label htmlFor="isHost" className="text-sm underline">
                                        This user is also a Host
                                    </label>
                                </div>
                            </Field>
                            
                            <Button type="submit">Create Account</Button>
                        </FieldGroup>
                    </form>
                </CardContent>
            </Card>
        </div>
    );
}